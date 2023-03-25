package com.example.burger_restaurant.services;

import com.example.burger_restaurant.domain.ItemQuantity;
import com.example.burger_restaurant.domain.Quarter;
import com.example.burger_restaurant.domain.YearData;
import com.example.burger_restaurant.domain.YearProfit;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DataAnalyzer {
    private List<DataItem> data;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yy");

    public DataAnalyzer(List<DataItem> data) {
        this.data = data;
    }

    public String getMostPopularItem() throws InterruptedException, ExecutionException {
        // Create a list of Callables to count the total quantity for each item
        List<Callable<ItemQuantity>> tasks = new ArrayList<>();
        Set<String> itemSet = new HashSet<>();
        for (DataItem item : data) {
            if (!itemSet.contains(item.getItem())) {
                itemSet.add(item.getItem());
                tasks.add(() -> {
                    int totalQuantity = 0;
                    for (DataItem currentItem : data) {
                        if (currentItem.getItem().equals(item.getItem())) {
                            totalQuantity += currentItem.getQuantity();
                        }
                    }
                    return new ItemQuantity(item.getItem(), totalQuantity);
                });
            }
        }

        // Use an ExecutorService to submit the tasks and get a list of Futures
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<ItemQuantity>> futures = executorService.invokeAll(tasks);

        // Find the item with the highest total quantity
        int maxQuantity = 0;
        String mostPopularItem = null;
        for (Future<ItemQuantity> future : futures) {
            ItemQuantity itemQuantity = future.get();
            int quantity = itemQuantity.getQuantity();
            if (quantity > maxQuantity) {
                maxQuantity = quantity;
                mostPopularItem = itemQuantity.getItem();
            }
        }

        // Shutdown the ExecutorService and return the result as a string
        executorService.shutdown();
        return "The most popular item is " + mostPopularItem + " with a total quantity of " + maxQuantity;
    }

    //average price for each year
    public String getAveragePricePerYear(List<DataItem> data) {
        // Create a map to store the total price and quantity for each year
        Map<Integer, YearData> yearTotals = new HashMap<>();

        // Create a thread pool to perform the calculations
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (DataItem item : data) {
            // Submit a task to the thread pool to update the totals for the year of the current item
            executor.submit(() -> {
                int year = item.getYear();
                double price = item.getPrice();
                int quantity = item.getQuantity();
                YearData yearData = yearTotals.get(year);
                if (yearData == null) {
                    yearData = new YearData();
                    yearTotals.put(year, yearData);
                }
                yearData.addToTotal(price * quantity);
                yearData.addToQuantity(quantity);
            });
        }

        // Wait for all the tasks to complete
        executor.shutdown();
        while (!executor.isTerminated()) {}

        // Calculate the average price for each year
        StringBuilder sb = new StringBuilder();
        for (int year : yearTotals.keySet()) {
            YearData yearData = yearTotals.get(year);
            double averagePrice = yearData.getTotal() / yearData.getQuantity();
            sb.append("Year ").append(year).append(": ").append(averagePrice).append("\n");
        }

        return sb.toString();
    }

    //average price for each quarter
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
    public String getMostProfitableYear(List<DataItem> data) throws ExecutionException, InterruptedException {
        // Create a list of FutureTasks to calculate the total profit for each year
        List<FutureTask<YearProfit>> tasks = new ArrayList<>();
        for (int year = 2000; year <= 2023; year++) {
            final int currentYear = year;
            FutureTask<YearProfit> task = new FutureTask<>(() -> {
                double totalProfit = 0.0;
                for (DataItem item : data) {
                    if (item.getYear() == currentYear) {
                        totalProfit += item.getPrice() * item.getQuantity();
                    }
                }
                return new YearProfit(currentYear, totalProfit);
            });
            tasks.add(task);
            Executors.newSingleThreadExecutor().submit(task);
        }

        // Wait for all the tasks to complete and find the year with the highest profit
        double maxProfit = Double.NEGATIVE_INFINITY;
        int mostProfitableYear = -1;
        for (FutureTask<YearProfit> task : tasks) {
            YearProfit yearProfit = task.get();
            double profit = yearProfit.getProfit();
            if (profit > maxProfit) {
                maxProfit = profit;
                mostProfitableYear = yearProfit.getYear();
            }
        }

        // Return the result as a string
        return "The most profitable year is " + mostProfitableYear + " with a total profit of " + maxProfit;
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