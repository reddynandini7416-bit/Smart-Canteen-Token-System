package com.canteen;

public class OrderItem {

    private int orderItemId;
    private int orderId;
    private int foodId;
    private int quantity;
    private double price;

    public OrderItem(int orderItemId, int orderId, int foodId, int quantity, double price) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItem(int orderId, int foodId, int quantity, double price) {
        this.orderId = orderId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getFoodId() {
        return foodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}