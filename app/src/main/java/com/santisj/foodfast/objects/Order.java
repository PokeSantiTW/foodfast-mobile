package com.santisj.foodfast.objects;

import java.math.BigDecimal;
import java.util.UUID;

public class Order {
    private FoodItem item;
    private int quantity;
    private double finalPrice;
    private String orderId;

    public Order(FoodItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.finalPrice = item.getPrice() * quantity;
        this.orderId = UUID.randomUUID().toString();
    }

    public Order() {
    }

    public FoodItem getItem() {
        return item;
    }

    public void setItem(FoodItem item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.finalPrice = item.getPrice() * quantity;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String  getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "item=" + item +
                ", quantity=" + quantity +
                ", finalPrice=" + finalPrice +
                ", orderId=" + orderId +
                '}';
    }
}
