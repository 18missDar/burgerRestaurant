package com.example.burger_restaurant.services;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class DataAnalyzer {
    private List<DataItem> data;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yy");

    public DataAnalyzer(List<DataItem> data) {
        this.data = data;
    }

    public String getMostPopularItem() {
        Map<String, Long> itemCounts = data.parallelStream()
                .collect(Collectors.groupingByConcurrent(DataItem::getItem, Collectors.counting()));

        // Get the most popular item
        return itemCounts.entrySet().parallelStream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No items found");
    }

    //average price for each year
    public String getAveragePricePerYear(List<DataItem> data) {
        Map<Integer, Double> result = data.parallelStream()
                .collect(Collectors.groupingByConcurrent(DataItem::getYear,
                        Collectors.averagingDouble(DataItem::getPrice)));

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            sb.append("Year: " + entry.getKey() + ", Average Price: " + entry.getValue() + "\n");
        }
        return sb.toString();
    }

    //average price for each quarter
    private static class Quarter {
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

    public static String getAveragePricePerQuarter(List<DataItem> dataItems) {
        Map<Quarter, List<DataItem>> itemsByQuarter = dataItems.parallelStream()
                .collect(Collectors.groupingByConcurrent(
                        di -> {
                            LocalDate date = LocalDate.parse(di.getDate(), DATE_FORMATTER);
                            int year = date.getYear();
                            int month = date.getMonthValue();
                            int quarter = (month - 1) / 3 + 1;
                            return new Quarter(year, quarter);
                        }
                ));

        Map<Quarter, Double> averagePriceByQuarter = new ConcurrentHashMap<>();
        itemsByQuarter.forEach((quarter, items) -> {
            double total = items.parallelStream().mapToDouble(DataItem::getPrice).sum();
            double average = total / items.size();
            averagePriceByQuarter.put(quarter, average);
        });

        return averagePriceByQuarter.entrySet().parallelStream()
                .map(entry -> String.format("%s: %.2f", entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

    //most profitable year
    public String getMostProfitableYear(List<DataItem> data) {
        Map<Integer, BigDecimal> result = data.parallelStream()
                .collect(Collectors.groupingByConcurrent(DataItem::getYear,
                        Collectors.mapping(item -> BigDecimal.valueOf(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        Map.Entry<Integer, BigDecimal> maxEntry = result.entrySet().parallelStream().max(Map.Entry.comparingByValue())
                .orElse(null);

        return maxEntry != null
                ? "Most profitable year: " + maxEntry.getKey() + ", Total Profit: " + maxEntry.getValue()
                : "No data found";
    }

    //most profitable  day
    public static String getMostProfitableDay(List<DataItem> data) {
        Map<String, BigDecimal> result = data.parallelStream()
                .collect(Collectors.groupingByConcurrent(DataItem::getDate,
                        Collectors.mapping(item -> BigDecimal.valueOf(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        Map.Entry<String, BigDecimal> maxEntry = result.entrySet().parallelStream()
                .max(Map.Entry.comparingByValue()).orElse(null);

        return maxEntry != null
                ? "Most profitable day: " + maxEntry.getKey() + ", Total Profit: "
                + maxEntry.getValue()
                : "No data found";
    }


    //which holiday was the most profitable for the institution, taking into account all the years
    public String getMostProfitableHoliday(List<DataItem> dataItems) {
        Map<String, Double> holidayProfits = dataItems.parallelStream()
                .filter(item -> !item.getHoliday().isEmpty())
                .collect(Collectors.groupingBy(
                        DataItem::getHoliday,
                        Collectors.summingDouble(item -> item.getPrice() * item.getQuantity())
                ));

        return holidayProfits.entrySet().parallelStream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("");
    }

    //the most profitable holiday for each year
    public String getMostProfitableHolidayPerYear(List<DataItem> dataItems) {
        Map<Integer, String> yearHolidayProfits = dataItems.parallelStream()
                .filter(item -> !item.getHoliday().isEmpty())
                .collect(Collectors.groupingBy(
                        DataItem::getYear,
                        Collectors.groupingBy(
                                DataItem::getHoliday,
                                Collectors.summingDouble(item -> item.getPrice() * item.getQuantity())
                        )
                ))
                .entrySet().parallelStream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().parallelStream()
                                .max(Comparator.comparingDouble(Map.Entry::getValue))
                                .map(Map.Entry::getKey)
                                .orElse("")
                                + " was the most profitable holiday"
                ));

        return yearHolidayProfits.entrySet().parallelStream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

}