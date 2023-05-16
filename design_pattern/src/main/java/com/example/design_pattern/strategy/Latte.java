package com.example.design_pattern.strategy;

public class Latte implements Coffee {
    @Override
    public String getDescription() {
        return "Latte";
    }

    @Override
    public double getCost() {
        return 2.49;
    }
}