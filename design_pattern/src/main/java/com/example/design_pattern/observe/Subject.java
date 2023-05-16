package com.example.design_pattern.observe;

/**
 * 主题接口，用于注册和删除观察者，以及通知观察者主题状态的改变。
 */
public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}