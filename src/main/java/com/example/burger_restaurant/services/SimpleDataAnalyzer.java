package com.example.burger_restaurant.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleDataAnalyzer {
    private List<DataItem> data;

    public SimpleDataAnalyzer(List<DataItem> data) {
        this.data = data;
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public String getMostPopularItem(List<DataItem> items) {
        Map<String, Long> counts = items.stream()
                .collect(Collectors.groupingBy(DataItem::getItem, Collectors.counting()));

        String mostPopularItem = counts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return mostPopularItem;
    }

    public String getAveragePriceByYear(List<DataItem> items) {
        Map<Integer, List<DataItem>> itemsByYear = items.stream()
                .collect(Collectors.groupingBy(DataItem::getYear));

        Map<Integer, Double> averagePricesByYear = new HashMap<>();

        for (Map.Entry<Integer, List<DataItem>> entry : itemsByYear.entrySet()) {
            int year = entry.getKey();
            List<DataItem> yearItems = entry.getValue();

            double total = 0.0;
            for (DataItem item : yearItems) {
                total += item.getPrice();
            }

            double averagePrice = total / yearItems.size();
            averagePricesByYear.put(year, averagePrice);
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Double> entry : averagePricesByYear.entrySet()) {
            int year = entry.getKey();
            double averagePrice = entry.getValue();
            sb.append("Year: " + year + ", Average Price: " + averagePrice + "\n");
        }

        return sb.toString();
    }

    public static String getAveragePriceByQuarter(List<DataItem> data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
        Map<Integer, List<BigDecimal>> quarters = data.stream()
                .collect(Collectors.groupingBy(item -> getQuarter(item.getDate()), Collectors.mapping(item -> BigDecimal.valueOf(item.getPrice()), Collectors.toList())));

        StringBuilder sb = new StringBuilder();
        quarters.forEach((quarter, prices) -> {
            BigDecimal avgPrice = prices.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(prices.size()), 2, BigDecimal.ROUND_HALF_UP);
            sb.append("Average price for quarter ").append(quarter).append(": $").append(avgPrice).append("\n");
        });
        return sb.toString();
    }

    private static int getQuarter(String date) {
        int month = Integer.parseInt(date.split("/")[0]);
        return (month - 1) / 3 + 1;
    }


    public String getMostProfitableYear(List<DataItem> items) {
        Map<Integer, Double> totalProfitsByYear = new HashMap<>();

        for (DataItem item : items) {
            int year = item.getYear();
            double profit = item.getPrice() * item.getQuantity();

            if (totalProfitsByYear.containsKey(year)) {
                double currentProfit = totalProfitsByYear.get(year);
                totalProfitsByYear.put(year, currentProfit + profit);
            } else {
                totalProfitsByYear.put(year, profit);
            }
        }

        int mostProfitableYear = 0;
        double maxProfit = 0.0;

        for (Map.Entry<Integer, Double> entry : totalProfitsByYear.entrySet()) {
            int year = entry.getKey();
            double totalProfit = entry.getValue();

            if (totalProfit > maxProfit) {
                maxProfit = totalProfit;
                mostProfitableYear = year;
            }
        }

        return "The most profitable year is " + mostProfitableYear + ", with a total profit of " + maxProfit;
    }

    public String getMostProfitableDay(List<DataItem> items) {
        Map<String, Double> totalProfitsByDay = new HashMap<>();

        for (DataItem item : items) {
            String date = item.getDate();
            double profit = item.getPrice() * item.getQuantity();

            if (totalProfitsByDay.containsKey(date)) {
                double currentProfit = totalProfitsByDay.get(date);
                totalProfitsByDay.put(date, currentProfit + profit);
            } else {
                totalProfitsByDay.put(date, profit);
            }
        }

        String mostProfitableDay = "";
        double maxProfit = 0.0;

        for (Map.Entry<String, Double> entry : totalProfitsByDay.entrySet()) {
            String date = entry.getKey();
            double totalProfit = entry.getValue();

            if (totalProfit > maxProfit) {
                maxProfit = totalProfit;
                mostProfitableDay = date;
            }
        }

        return "The most profitable day is " + mostProfitableDay.toString() + ", with a total profit of " + maxProfit;
    }

    public String getMostProfitableHoliday(List<DataItem> items) {
        Map<String, Double> totalProfitsByHoliday = new HashMap<>();

        for (DataItem item : items) {
            String holiday = item.getHoliday();
            if (!holiday.isEmpty()) {
                double profit = item.getPrice() * item.getQuantity();

                if (totalProfitsByHoliday.containsKey(holiday)) {
                    double currentProfit = totalProfitsByHoliday.get(holiday);
                    totalProfitsByHoliday.put(holiday, currentProfit + profit);
                } else {
                    totalProfitsByHoliday.put(holiday, profit);
                }
            }
        }

        String mostProfitableHoliday = null;
        double maxProfit = 0.0;

        for (Map.Entry<String, Double> entry : totalProfitsByHoliday.entrySet()) {
            String holiday = entry.getKey();
            double totalProfit = entry.getValue();

            if (totalProfit > maxProfit) {
                maxProfit = totalProfit;
                mostProfitableHoliday = holiday;
            }
        }

        return "The most profitable holiday is " + mostProfitableHoliday + ", with a total profit of " + maxProfit;
    }

    public String getMostProfitableHolidayByYear(List<DataItem> items) {
        Map<Integer, Map<String, Double>> totalProfitsByYearAndHoliday = new HashMap<>();

        for (DataItem item : items) {
            int year = item.getYear();
            String holiday = item.getHoliday();
            if (!holiday.isEmpty()) {
                double profit = item.getPrice() * item.getQuantity();

                if (totalProfitsByYearAndHoliday.containsKey(year)) {
                    Map<String, Double> totalProfitsByHoliday = totalProfitsByYearAndHoliday.get(year);

                    if (totalProfitsByHoliday.containsKey(holiday)) {
                        double currentProfit = totalProfitsByHoliday.get(holiday);
                        totalProfitsByHoliday.put(holiday, currentProfit + profit);
                    } else {
                        totalProfitsByHoliday.put(holiday, profit);
                    }
                } else {
                    Map<String, Double> totalProfitsByHoliday = new HashMap<>();
                    totalProfitsByHoliday.put(holiday, profit);
                    totalProfitsByYearAndHoliday.put(year, totalProfitsByHoliday);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Map<String, Double>> entry : totalProfitsByYearAndHoliday.entrySet()) {
            int year = entry.getKey();
            Map<String, Double> result = entry.getValue();
            sb.append("Year" + year + "with most profitable holiday: " + result.keySet() + " and total profit: " + result.values());
        }
        return sb.toString();
    }




}
