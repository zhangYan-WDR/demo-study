package com.zy.wxpay.service;

import java.util.Map;

public interface Wx2PayService {
    Map<String, Object> createNative(String orderNo);

    Map<String, String> queryPayStatus(String orderNo);

    Map<String, String> refund(String orderNo);
}
