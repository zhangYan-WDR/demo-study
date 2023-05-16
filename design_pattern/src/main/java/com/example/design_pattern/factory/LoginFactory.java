package com.example.design_pattern.factory;

public class LoginFactory {
    //创建一个登录工厂类，用于根据登录类型创建对应的登录对象。
    //该工厂类应该包含一个方法，接收登录类型作为参数，返回对应的登录对象。
    public static LoginStrategy createLoginStrategy(String type) {
        if ("password".equals(type)) {
            return new PasswordLogin();
        } else if ("phone".equals(type)) {
            return new PhoneLogin();
        } else {
            // 处理其他登录类型
            return null;
        }
    }
}