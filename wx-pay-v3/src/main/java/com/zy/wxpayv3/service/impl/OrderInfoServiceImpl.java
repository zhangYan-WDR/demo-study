package com.zy.wxpayv3.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.wxpayv3.entity.OrderInfo;
import com.zy.wxpayv3.mapper.OrderInfoMapper;
import com.zy.wxpayv3.service.OrderInfoService;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

}
