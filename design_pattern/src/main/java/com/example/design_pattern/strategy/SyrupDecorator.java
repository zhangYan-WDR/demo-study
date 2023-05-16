package com.example.design_pattern.strategy;

public class SyrupDecorator extends CoffeeDecorator {
    public SyrupDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Syrup";
    }

    @Override
    public double getCost() {
        return super.getCost() + 0.3;
    }
}