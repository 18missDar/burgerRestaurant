package com.example.burger_restaurant.domain;

public class ItemQuantity {
    private final String item;
    private final int quantity;

    public ItemQuantity(String item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
}
