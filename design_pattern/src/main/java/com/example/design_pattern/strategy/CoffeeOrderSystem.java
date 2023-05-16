package com.example.design_pattern.strategy;

public class CoffeeOrderSystem {

    public static void main(String[] args) {
        // 创建 Espresso 咖啡订单
        Coffee espresso = new Espresso();
        System.out.println("Order: " + espresso.getDescription());
        System.out.println("Cost: $" + espresso.getCost());

        // 创建 Latte 咖啡订单，并添加牛奶和糖浆
        Coffee latte = new Latte();
        latte = new MilkDecorator(latte);
        latte = new SyrupDecorator(latte);
        System.out.println("Order: " + latte.getDescription());
        System.out.println("Cost: $" + latte.getCost());

        // 创建 Cappuccino 咖啡订单，并添加牛奶、糖浆和奶泡
        Coffee cappuccino = new Cappuccino();
        cappuccino = new MilkDecorator(cappuccino);
        cappuccino = new SyrupDecorator(cappuccino);
        cappuccino = new FoamDecorator(cappuccino);
        System.out.println("Order: " + cappuccino.getDescription());
        System.out.println("Cost: $" + cappuccino.getCost());
    }

}
