package com.example.burger_restaurant.services;

import java.math.BigDecimal;

public class DataItem {
    private String date;
    private double price;
    private int quantity;
    private int sellId;
    private String sellCategory;
    private String item;
    private int year;
    private String holiday;
    private boolean weekend;
    private boolean schoolBreak;
    private double averageTemperature;
    private boolean outdoor;

    // Constructor, getters, and setters

    public DataItem() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSellId() {
        return sellId;
    }

    public void setSellId(int sellId) {
        this.sellId = sellId;
    }

    public String getSellCategory() {
        return sellCategory;
    }

    public void setSellCategory(String sellCategory) {
        this.sellCategory = sellCategory;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    public boolean isSchoolBreak() {
        return schoolBreak;
    }

    public void setSchoolBreak(boolean schoolBreak) {
        this.schoolBreak = schoolBreak;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public boolean isOutdoor() {
        return outdoor;
    }

    public void setOutdoor(boolean outdoor) {
        this.outdoor = outdoor;
    }
}
