package com.example.design_pattern.observe;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现一个主题类，用于保存天气数据，并在数据发生改变时通知已注册的观察者
 */
public class WeatherData implements Subject {
    private List<Observer> observers;//注册进来的所有的观察者列表
    private String temperature;//温度
    private String humidity;//湿度
    private String pressure;//压力

    public WeatherData() {
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(temperature, humidity, pressure);
        }
    }

    public void setMeasurements(String temperature, String humidity, String pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        measurementsChanged();
    }

    public void measurementsChanged() {
        notifyObservers();
    }
}