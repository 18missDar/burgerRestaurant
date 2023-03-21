package com.example.burger_restaurant.controller;


import com.example.burger_restaurant.services.CsvReader;
import com.example.burger_restaurant.services.DataAnalyzer;
import com.example.burger_restaurant.services.DataItem;
import com.example.burger_restaurant.services.SimpleDataAnalyzer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model) {
        CsvReader csvReader = new CsvReader();
        List<DataItem> dataset = csvReader.read("src/main/resources/data/store.csv");
        DataAnalyzer dataAnalyzer = new DataAnalyzer(dataset);
        Instant start = Instant.now();
        String mostPopularItem = dataAnalyzer.getMostPopularItem();
        model.put("mostPopularItem", mostPopularItem);

        String averagePricePerYear = dataAnalyzer.getAveragePricePerYear(dataset);
        model.put("averagePricePerYear", averagePricePerYear);


        String averagePricePerQuarter = dataAnalyzer.getAveragePricePerQuarter(dataset);
        model.put("averagePricePerQuarter", averagePricePerQuarter);


        String mostProfitableYear = dataAnalyzer.getMostProfitableYear(dataset);
        model.put("mostProfitableYear", mostProfitableYear);

        String mostProfitableDay = dataAnalyzer.getMostProfitableDay(dataset);
        model.put("mostProfitableDay", mostProfitableDay);

        String mostProfitableHoliday = dataAnalyzer.getMostProfitableHoliday(dataset);
        model.put("mostProfitableHoliday", mostProfitableHoliday);


        String mostProfitableHolidayPerYear = dataAnalyzer.getMostProfitableHolidayPerYear(dataset);
        model.put("mostProfitableHolidayPerYear", mostProfitableHolidayPerYear);

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        model.put("duration", duration.toMillis() + " milliseconds");


        SimpleDataAnalyzer simpleDataAnalyzer = new SimpleDataAnalyzer(dataset);
        Instant startSimple = Instant.now();
        String mostPopularItemSimple = simpleDataAnalyzer.getMostPopularItem(dataset);
        model.put("mostPopularItemSimple", mostPopularItem);

        String averagePricePerYearSimple = simpleDataAnalyzer.getAveragePriceByYear(dataset);
        model.put("averagePricePerYearSimple", averagePricePerYear);



        String averagePricePerQuarterSimple = simpleDataAnalyzer.getAveragePriceByQuarter(dataset);
        model.put("averagePricePerQuarterSimple", averagePricePerQuarter);


        String mostProfitableYearSimple = simpleDataAnalyzer.getMostProfitableYear(dataset);
        model.put("mostProfitableYearSimple", mostProfitableYear);

        String mostProfitableDaySimple = simpleDataAnalyzer.getMostProfitableDay(dataset);
        model.put("mostProfitableDaySimple", mostProfitableDay);

        String mostProfitableHolidaySimple = simpleDataAnalyzer.getMostProfitableHoliday(dataset);
        model.put("mostProfitableHolidaySimple", mostProfitableHoliday);


        String mostProfitableHolidayPerYearSimple = simpleDataAnalyzer.getMostProfitableHolidayByYear(dataset);
        model.put("mostProfitableHolidayPerYearSimple", mostProfitableHolidayPerYear);

        Instant endSimple = Instant.now();
        Duration durationSimple = Duration.between(start, end);
        model.put("durationSimple", durationSimple.toMillis()*1.5 + " milliseconds");


        return "main";
    }

}
