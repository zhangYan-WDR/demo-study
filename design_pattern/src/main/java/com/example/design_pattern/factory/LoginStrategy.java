package com.example.design_pattern.factory;

import java.util.Map;

//创建登录策略接口（LoginStrategy），定义统一的登录方法。
public interface LoginStrategy {
    Map<String, Object> login(UserLoginRequest request, String type);
}