package com.example.design_pattern.factory;

import org.junit.platform.commons.util.StringUtils;

import java.util.Map;

public class LoginController {

    //具体调用实现的方法
    public Map<String, Object> login(UserLoginRequest request, String type) {
        if (StringUtils.isBlank(request.getLoginType())) {
            // 省略原有的逻辑
        }

        // 使用工厂类创建对应的登录策略对象
        LoginStrategy loginStrategy = LoginFactory.createLoginStrategy(type);
        if (loginStrategy != null) {
            return loginStrategy.login(request, type);
        }

        // 处理其他登录类型的逻辑
        // ...

        return null;
    }
}
