package com.example.design_pattern.factory;

import java.util.Map;

public class PasswordLogin implements LoginStrategy {
    @Override
    public Map<String, Object> login(UserLoginRequest request, String type) {
        // 密码登录的具体实现
        return null;
    }
}