package com.example.design_pattern.observe;

/**
 * 最后实现观察者类，可以是手机应用程序、网站或其他组件，用于接收天气数据的更新通知，并做出相应的响应。
 */
public class PhoneApp implements Observer {
    private String temperature;
    private String humidity;

    @Override
    public void update(String temperature, String humidity, String pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        display();
    }

    public void display() {
        System.out.println("Phone App: Temperature = " + temperature + ", Humidity = " + humidity);
    }
}
