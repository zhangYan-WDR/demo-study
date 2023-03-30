package com.zy.wxpayv3.service.impl;

import com.google.gson.Gson;
import com.zy.wxpayv3.config.WxPayConfig;
import com.zy.wxpayv3.entity.OrderInfo;
import com.zy.wxpayv3.enums.OrderStatus;
import com.zy.wxpayv3.enums.wxpay.WxApiType;
import com.zy.wxpayv3.enums.wxpay.WxNotifyType;
import com.zy.wxpayv3.service.WxPayService;
import com.zy.wxpayv3.util.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    @Resource
    private WxPayConfig wxPayConfig;

    @Resource
    private CloseableHttpClient wxPayClient;

    @Override
    public Map<String, Object> nativePay(Long productId) throws Exception {
        //生成订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTitle("test");
        orderInfo.setOrderNo(OrderNoUtils.getOrderNo());
        orderInfo.setProductId(productId);
        orderInfo.setTotalFee(1);
        orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());
        //入库

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
            String codeUrl = resultMap.get("code_url");

            Map<String, Object> map = new HashMap<>();
            map.put("codeUrl", codeUrl);
            map.put("orderNo", orderInfo.getOrderNo());
            return map;
        } finally {
            response.close();
        }
    }
}
