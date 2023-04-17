package com.zy.wxpay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;
import com.zy.wxpay.service.Wx2PayService;
import com.zy.wxpay.util.HttpClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class Wx2PayServiceImpl implements Wx2PayService {

    @Override
    public Map<String, Object> createNative(String orderNo) {
        try {
            //1查询订单信息
//            QueryWrapper<TOrder> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("order_no", orderNo);
//            TOrder order = orderService.getOne(queryWrapper);
//            //1.1校验订单
//            if (order == null) {
//                throw new GuliException(20001, "订单失效");
//            }
            //2封装支付参数
            Map m = new HashMap();
            //设置支付参数
            m.put("appid", "wx74862e0dfcf69954");//必填应用id
            m.put("mch_id", "1558950191");//必填商户号
            m.put("nonce_str", WXPayUtil.generateNonceStr());//必填微信生成随机字符串
            m.put("body", "测试商品");//自己的业务内容课程名称
            m.put("out_trade_no", orderNo);//订单编号
            m.put("total_fee", new BigDecimal("0.1").multiply(new BigDecimal("100")).longValue() + "");//交易金额
            m.put("spbill_create_ip", "127.0.0.1");//终端ip
            m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify\n");//必填回调地址
            m.put("trade_type", "NATIVE");//交易类型
            //3通过httpclient发送请求,参数转化成xml
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");//官方请求地址

            //client设置参数
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));//必填商户密钥
            client.setHttps(true);
            client.post();
            //4 获取返回值
            String xml = client.getContent();
            System.out.println("xml=" + xml);
            //解析返回map集合
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //5、封装返回结果集

            Map map = new HashMap<>();
            map.put("out_trade_no", orderNo);//订单号
            map.put("course_id", UUID.randomUUID().toString());//自定义的商品id
            map.put("total_fee", new BigDecimal("0.1"));//金额
            map.put("result_code", resultMap.get("result_code"));//支付结果码
            map.put("code_url", resultMap.get("code_url"));//支付二维码链接
            return map;

        } catch (Exception e) {
            e.printStackTrace();
//            throw new Exception(20001, "获取二维码失败");

        }
        return null;
    }

    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try {
            //1封装支付参数
            Map m = new HashMap();
            //设置支付参数
            m.put("appid", "wx74862e0dfcf69954");//必填应用id
            m.put("mch_id", "1558950191");//必填商户号
            m.put("out_trade_no", orderNo);//必填订单号
            m.put("nonce_str", WXPayUtil.generateNonceStr());//微信工具生成随机字符
            //2、设置请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");//官方查询请求地址
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));//商户密钥
            client.setHttps(true);
            client.post();
            //3、返回第三方的数据
            String xml = client.getContent();
            System.out.println("订单状态="+xml);
            //转义成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //6、转成Map
            //7、返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
//            throw new GuliException(20001,"查询订单状态失败");
        }
        return null;

    }

    @Override
    public Map<String, String> refund(String orderNo) {
        try {
            //1封装支付参数
            Map m = new HashMap();
            //设置支付参数
            m.put("appid", "wx74862e0dfcf69954");//必填应用id
            m.put("mch_id", "1558950191");//必填商户号
            m.put("out_trade_no", orderNo);//必填订单号
//            m.put("nonce_str", WXPayUtil.generateNonceStr());//微信工具生成随机字符
            m.put("out_refund_no", WXPayUtil.generateNonceStr());//微信工具生成随机字符
            m.put("reason", "我不想要了");
            Map<String, Object> amountMap = new HashMap<>();
            amountMap.put("refund",new BigDecimal("0.1").multiply(new BigDecimal("100")).intValue());
            amountMap.put("total", new BigDecimal("0.1").multiply(new BigDecimal("100")).intValue());
            amountMap.put("currency", "CNY");
            m.put("amount", JSONObject.toJSONString(amountMap));
            //2、设置请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds");//官方查询请求地址
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));//商户密钥
            client.setHttps(true);
            client.post();
            //3、返回第三方的数据
            String xml = client.getContent();
            System.out.println("订单状态="+xml);
            //转义成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //6、转成Map
            //7、返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
//            throw new GuliException(20001,"查询订单状态失败");
        }
        return null;
    }
}
