package com.example.burger_restaurant.domain;

public class YearData {
    private double total;
    private int quantity;

    public YearData() {
        this.total = 0.0;
        this.quantity = 0;
    }

    public synchronized void addToTotal(double value) {
        total += value;
    }

    public synchronized void addToQuantity(int value) {
        quantity += value;
    }

    public synchronized double getTotal() {
        return total;
    }

    public synchronized int getQuantity() {
        return quantity;
    }
}
