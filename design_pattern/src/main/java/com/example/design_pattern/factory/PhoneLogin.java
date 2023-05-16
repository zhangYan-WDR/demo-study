package com.example.design_pattern.factory;

import java.util.Map;

public class PhoneLogin implements LoginStrategy {
    @Override
    public Map<String, Object> login(UserLoginRequest request, String type) {
        // 手机号登录的具体实现
        return null;
    }
}