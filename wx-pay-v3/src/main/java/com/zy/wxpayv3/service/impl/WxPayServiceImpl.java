package com.zy.wxpayv3.service.impl;

import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.zy.wxpayv3.config.WxPayConfig;
import com.zy.wxpayv3.entity.OrderInfo;
import com.zy.wxpayv3.enums.OrderStatus;
import com.zy.wxpayv3.enums.wxpay.WxApiType;
import com.zy.wxpayv3.enums.wxpay.WxNotifyType;
import com.zy.wxpayv3.enums.wxpay.WxTradeState;
import com.zy.wxpayv3.service.OrderInfoService;
import com.zy.wxpayv3.service.PaymentInfoService;
import com.zy.wxpayv3.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    @Resource
    private WxPayConfig wxPayConfig;

    @Resource
    private CloseableHttpClient wxPayClient;

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private PaymentInfoService paymentInfoService;

    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public Map<String, Object> nativePay(Long productId) throws Exception {

        OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);

        //防止一直调用微信统一下单API
        String codeUrl = orderInfo.getCodeUrl();
        if (orderInfo != null && !StringUtils.isEmpty(codeUrl)) {
            log.info("订单已存在,二维码已保存");
            Map<String, Object> map = new HashMap<>();
            map.put("codeUrl", codeUrl);
            map.put("orderNo", orderInfo.getOrderNo());
            return map;
        }

        log.info("调用统一下单API");
        //调用统一下单API
        HttpPost httpPost = new HttpPost(wxPayConfig.getDomain().concat(WxApiType.NATIVE_PAY.getType()));
        // 请求body参数
        Gson gson = new Gson();
        Map paramsMap = new HashMap();
        paramsMap.put("appid",wxPayConfig.getAppid());
        paramsMap.put("mchid",wxPayConfig.getMchId());
        paramsMap.put("description",orderInfo.getTitle());
        paramsMap.put("out_trade_no",orderInfo.getOrderNo());
        paramsMap.put("notify_url",wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));
        HashMap amountMap = new HashMap();
        amountMap.put("total", orderInfo.getTotalFee());
        amountMap.put("currency", "CNY");
        paramsMap.put("amount",amountMap);
        String reqdata = gson.toJson(paramsMap);
        log.info("请求参数:{}",reqdata);
        StringEntity entity = new StringEntity(reqdata,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpPost);

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            String bodyAsString = EntityUtils.toString(response.getEntity());
            if (statusCode == 200) { //处理成功
                log.info("成功,返回结果 = " + bodyAsString);
            } else if (statusCode == 204) { //处理成功，无返回Body
                log.info("成功");
            } else {
                log.info("失败,响应码 = " + statusCode+ ",响应体 = " + bodyAsString);
                throw new IOException("请求失败");
            }
            Map<String,String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
            codeUrl = resultMap.get("code_url");
            //将二维码保存入库
            //TODO 二维码有效期为2小时，设置二维码有效期
            orderInfoService.saveCodeUrl(orderInfo.getOrderNo(),codeUrl);

            Map<String, Object> map = new HashMap<>();
            map.put("codeUrl", codeUrl);
            map.put("orderNo", orderInfo.getOrderNo());
            return map;
        } finally {
            response.close();
        }
    }

    @Override
    public void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException {
        log.info("处理订单");
        String plainText = decryptFromResorce(bodyMap);
        //将明文转化为map
        Gson gson = new Gson();
        Map plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String) plainTextMap.get("out_trade_no");
        /**
         * 在对业务数据进行状态检查和处理之前
         * 要采用数据锁进行并发控制
         * 以避免函数重入造成的数据混乱
         */
        if (lock.tryLock()) {
            try {
                //防止通知被一直执行，处理重复的通知
                String orderStatus = orderInfoService.getOrderStatus(orderNo);
                if (!OrderStatus.NOTPAY.getType().equals(orderStatus)) {
                    return;
                }
                //更改订单状态
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
                //记录支付日志
                paymentInfoService.createPaymentInfo(plainText);
            }finally {
                lock.unlock();
            }
        }
    }

    /**
     * 对称解密
     * 将微信回调返回的信息进行相同的算法进行解密，拿到报文返回的明文数据
     * @param bodyMap
     * @return
     */
    private String decryptFromResorce(Map<String, Object> bodyMap) throws GeneralSecurityException {
        log.info("开始进行微信回调返回的密文解密");
        //微信解密需要aesUtil进行对密文解密，构造函数需要一个byte类型的密钥
        AesUtil aesUtil = new AesUtil(wxPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        Map<String,String> resource = (Map<String, String>) bodyMap.get("resource");
        String ciphertext = resource.get("ciphertext");
        String nonce = resource.get("nonce");
        String associatedData = resource.get("associated_data");
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8), nonce.getBytes(StandardCharsets.UTF_8), ciphertext);
        log.info("解密后的明文为--->{},密文为--->{}",plainText,ciphertext);
        return plainText;
    }


    /**
     * 取消订单接口
     * @param orderNo
     */
    @Override
    public void cancelOrder(String orderNo) throws Exception {
        //1.调用微信关单接口
        this.closeOrder(orderNo);
        //2.处理客户端订单状态
        orderInfoService.updateStatusByOrderNo(orderNo,OrderStatus.CANCEL);
    }

    private void closeOrder(String orderNo) throws Exception {
        //调用native关单接口
        log.info("调用微信关单接口 订单号为--->{}",orderNo);
        String cancelUrl = String.format(WxApiType.CLOSE_ORDER_BY_NO.getType(), orderNo);
        HttpPost httpPost = new HttpPost(wxPayConfig.getDomain().concat(cancelUrl));
        // 请求body参数
        Gson gson = new Gson();
        Map paramsMap = new HashMap();
        paramsMap.put("mchid",wxPayConfig.getMchId());
        String reqdata = gson.toJson(paramsMap);
        log.info("请求参数:{}",reqdata);
        StringEntity entity = new StringEntity(reqdata,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpPost);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 204) { //处理成功，无返回Body
                log.info("成功");
            } else {
                log.info("失败,响应码 = " + statusCode);
                throw new IOException("请求失败");
            }
        } finally {
            response.close();
        }
    }

    @Override
    public String queryOrder(String orderNo) throws Exception {
        //调用native查询订单接口
        log.info("调用微信查询订单接口 订单号为--->{}",orderNo);
        String queryUrl = String.format(WxApiType.ORDER_QUERY_BY_NO.getType(), orderNo);
        HttpGet httpGet = new HttpGet(wxPayConfig.getDomain().concat(queryUrl).concat("?mchid=").concat(wxPayConfig.getMchId()));
        httpGet.setHeader("Accept", "application/json");
        //完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpGet);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            String bodyAsString = EntityUtils.toString(response.getEntity());
            if (statusCode == 200) { //处理成功
                log.info("成功,返回结果 = " + bodyAsString);
            } else if (statusCode == 204) { //处理成功，无返回Body
                log.info("成功");
            } else {
                log.info("失败,响应码 = " + statusCode+ ",响应体 = " + bodyAsString);
                throw new IOException("请求失败");
            }
            return bodyAsString;
        } finally {
            response.close();
        }
    }

    /**
     * 根据订单号查询微信支付查单接口，核实订单状态
     * 如果订单已支付，更新商户端的订单状态
     * 如果订单未支付，则调用关单接口关闭订单，并更新客户端的订单状态
     * @param orderNo
     */
    @Override
    public void checkOrderStatus(String orderNo) throws Exception {
        String result = queryOrder(orderNo);
        Gson gson = new Gson();
        Map<String,Object> resultMap = gson.fromJson(result, Map.class);
        String tradeState = (String) resultMap.get("trade_state");
        if (WxTradeState.SUCCESS.getType().equals(tradeState)) {
            log.warn("核实订单已经支付，===>{}",orderNo);
            //更新本地订单状态
            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
            //记录支付日志
            paymentInfoService.createPaymentInfo(result);
            return;
        }
        if (WxTradeState.NOTPAY.getType().equals(tradeState)) {
            log.warn("核实订单未支付,===>{}", orderNo);
            //如果订单为支付，则调用关单接口，更新本地订单状态
            this.closeOrder(orderNo);

            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
        }

    }
}
