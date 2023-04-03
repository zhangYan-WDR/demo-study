package com.zy.wxpayv3.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.zy.wxpayv3.entity.PaymentInfo;
import com.zy.wxpayv3.enums.PayType;
import com.zy.wxpayv3.mapper.PaymentInfoMapper;
import com.zy.wxpayv3.service.OrderInfoService;
import com.zy.wxpayv3.service.PaymentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Resource
    private OrderInfoService orderInfoService;

    /**
     * 创建支付日志
     * @param plainText 微信回调返回的密文通过解密之后的明文
     */
    @Override
    public void createPaymentInfo(String plainText) {
        log.info("开始创建支付日志");
        Gson gson = new Gson();
        Map plainTextMap = gson.fromJson(plainText, HashMap.class);
        //订单号
        String orderNo = (String) plainTextMap.get("out_trade_no");
        //交易编号
        String transactionId = (String) plainTextMap.get("transaction_id");
        //交易类型
        String tradeType = (String) plainTextMap.get("trade_type");
        //交易状态
        String tradeState = (String) plainTextMap.get("trade_state");
        //订单金额
        Map<String,Object> amount = (Map<String, Object>) plainTextMap.get("amount");
        //用户实付金额
        Integer payerTotal = ((Double) amount.get("payer_total")).intValue();
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderNo(orderNo);
        paymentInfo.setPaymentType(PayType.WXPAY.getType());
        paymentInfo.setTransactionId(transactionId);
        paymentInfo.setTradeType(tradeType);
        paymentInfo.setTradeState(tradeState);
        paymentInfo.setPayerTotal(payerTotal);
        paymentInfo.setContent(plainText);
        baseMapper.insert(paymentInfo);
        log.info("支付日志创建成功");
    }

}
