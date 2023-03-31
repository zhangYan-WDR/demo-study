package com.zy.wxpayv3.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.wxpayv3.entity.OrderInfo;

public interface OrderInfoService extends IService<OrderInfo> {

    OrderInfo createOrderByProductId(Long productId);

    void saveCodeUrl(String orderNo, String codeUrl);
}
