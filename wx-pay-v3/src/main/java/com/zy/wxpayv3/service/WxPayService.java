package com.zy.wxpayv3.service;

import java.util.Map;

public interface WxPayService {
    Map<String, Object> nativePay(Long productId) throws Exception;
}
