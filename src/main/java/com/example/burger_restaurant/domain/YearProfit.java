package com.example.burger_restaurant.domain;

public class YearProfit {
    private final int year;
    private final double profit;

    public YearProfit(int year, double profit) {
        this.year = year;
        this.profit = profit;
    }

    public int getYear() {
        return year;
    }

    public double getProfit() {
        return profit;
    }
}
