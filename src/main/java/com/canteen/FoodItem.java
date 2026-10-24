package com.canteen;

public class FoodItem {

    private int foodId;
    private String foodName;
    private double price;
    private boolean availability;

    public FoodItem(int foodId, String foodName, double price, boolean availability) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.price = price;
        this.availability = availability;
    }

    public FoodItem(String foodName, double price, boolean availability) {
        this.foodName = foodName;
        this.price = price;
        this.availability = availability;
    }

    public int getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return availability;
    }
}