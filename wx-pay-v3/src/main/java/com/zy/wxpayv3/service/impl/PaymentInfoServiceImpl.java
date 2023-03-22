package com.zy.wxpayv3.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.wxpayv3.entity.PaymentInfo;
import com.zy.wxpayv3.mapper.PaymentInfoMapper;
import com.zy.wxpayv3.service.PaymentInfoService;
import org.springframework.stereotype.Service;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

}
