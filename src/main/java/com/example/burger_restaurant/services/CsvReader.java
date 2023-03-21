package com.example.burger_restaurant.services;

import antlr.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    private static final String DELIMITER = ",";

    public List<DataItem> read(String filePath) {
        List<DataItem> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header line
                }
                String[] fields = line.split(DELIMITER);
                DataItem item = new DataItem();
                item.setDate(fields[0]);
                item.setPrice(Double.parseDouble(fields[1]));
                item.setQuantity(Integer.parseInt(fields[2]));
                item.setSellId(Integer.parseInt(fields[3]));
                item.setSellCategory(fields[4]);
                item.setItem(fields[5]);
                item.setYear(Integer.parseInt(fields[6]));
                item.setHoliday(fields[7]);
                item.setWeekend(Boolean.parseBoolean(fields[8]));
                item.setSchoolBreak(Boolean.parseBoolean(fields[9]));
                item.setAverageTemperature(Double.parseDouble(fields[10]));
                item.setOutdoor(Boolean.parseBoolean(fields[11]));
                data.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}