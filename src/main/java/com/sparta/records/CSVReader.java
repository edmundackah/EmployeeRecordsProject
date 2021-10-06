package com.sparta.records;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CSVReader {
    private static CSVReader instance;

    private CSVReader() {
    }

    public static CSVReader getInstance() {
        if (instance == null) {
            instance = new CSVReader();
        }
        return instance;
    }

    public List<Employee> getEmployeeRecords(String path) {
        List<Employee> employees = new ArrayList<>();

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

        return employees;
    }
}
