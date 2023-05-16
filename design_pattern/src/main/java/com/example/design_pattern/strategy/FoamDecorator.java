package com.example.design_pattern.strategy;

public class FoamDecorator extends CoffeeDecorator {
    public FoamDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Foam";
    }

    @Override
    public double getCost() {
        return super.getCost() + 0.2;
    }
}