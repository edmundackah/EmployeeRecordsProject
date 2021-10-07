package com.sparta.records;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class CSVReader {
    private static CSVReader instance;
    private Set<Employee> uniqueRecords;
    private List<Employee> employees, duplicateRecords;

    private String path;

    private CSVReader(String path) {
        this.path = path;
        parseRecordsFromCSV();
    }

    public static CSVReader getInstance(String path) {
        if (instance == null) {
            instance = new CSVReader(path);
        }
        return instance;
    }

    public Set<Employee> getUniqueRecords() {
        return uniqueRecords;
    }

    public List<Employee> getRecords() {
        return employees;
    }

    public List<Employee> getDuplicateRecords() {
        return duplicateRecords;
    }

    private void findDuplicates() {

    }

    private void parseRecordsFromCSV() {
        employees = new ArrayList<>();

        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String line;
            int lineCount = 1;
            while((line = bf.readLine()) != null) {
                String[] temp = line.split(",");

                //skip first line of CSV file (header info)
                if (lineCount > 1) {
                    try {
                        employees.add(
                                new Employee(temp[1], temp[2], temp[4], temp[6], temp[3].charAt(0),
                                        temp[5].charAt(0), temp[7], Integer.parseInt(temp[0]),
                                        Integer.parseInt(temp[9]), temp[8])
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch(NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //creating hashset
        uniqueRecords = new HashSet();
        uniqueRecords.addAll(employees);
    }
}