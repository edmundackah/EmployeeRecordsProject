package com.sparta.records;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        CSVReader csvReader = CSVReader.getInstance("src/main/EmployeeRecordsLarge.csv");
        int cores = Runtime.getRuntime().availableProcessors();

        List<Employee> records = csvReader.getRecords();

        System.out.println(csvReader.getDuplicateRecords().size());

        JDBCDriver jdbcDriver = new JDBCDriver(1);
        jdbcDriver.writeRecords(csvReader.getUniqueRecords().stream().toList());


    }
}
