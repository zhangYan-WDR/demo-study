package com.example.design_pattern.observe;

public class WeatherStation {

    public static void main(String[] args) {
        WeatherData weatherData = new WeatherData();

        PhoneApp phoneApp = new PhoneApp();
        Website website = new Website();

        weatherData.registerObserver(phoneApp);
        weatherData.registerObserver(website);

        weatherData.setMeasurements("25℃", "80%", "1013hPa");

        weatherData.removeObserver(phoneApp);
        weatherData.setMeasurements("28℃", "75%", "1015hPa");
    }

    //以上代码演示了观察者模式的实现。
    // 当天气数据发生变化时，主题类`WeatherData`会通知已注册的观察者，并调用其`update`方法进行更新。
    // 观察者根据收到的天气数据进行相应的处理和展示。
}
