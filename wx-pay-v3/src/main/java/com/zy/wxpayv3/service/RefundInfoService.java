package com.zy.wxpayv3.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.wxpayv3.entity.RefundInfo;

public interface RefundInfoService extends IService<RefundInfo> {

    RefundInfo createRefundByOrderNo(String orderNo, String reason);

    void updateRefund(String content);
}
