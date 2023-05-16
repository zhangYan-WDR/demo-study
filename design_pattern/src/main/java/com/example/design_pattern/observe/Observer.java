package com.example.design_pattern.observe;

/**
 * 定义一个观察者接口，用于接收天气数据的更新通知。
 * 观察者接口一般包含一个更新方法，当观察的主题状态发生改变时，调用该方法通知观察者。
 */
public interface Observer {
    void update(String temperature, String humidity, String pressure);
}