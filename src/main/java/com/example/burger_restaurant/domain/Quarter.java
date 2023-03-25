package com.example.burger_restaurant.domain;

import com.example.burger_restaurant.services.DataAnalyzer;

public class Quarter {
    private final int year;
    private final int quarter;

    public Quarter(int year, int quarter) {
        this.year = year;
        this.quarter = quarter;
    }

    @Override
    public int hashCode() {
        return year * 10 + quarter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Quarter)) {
            return false;
        }
        Quarter other = (Quarter) obj;
        return this.year == other.year && this.quarter == other.quarter;
    }

    @Override
    public String toString() {
        return String.format("%d Q%d", year, quarter);
    }
}
