package com.sparta.records;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        CSVReader csvReader = CSVReader.getInstance("src/main/EmployeeRecords.csv");
        int cores = Runtime.getRuntime().availableProcessors();

        Set<Employee> uniqueRecords = new HashSet<>();
        List<Employee> records = csvReader.getRecords();

        System.out.println(records.size());

        //get only unique records
        uniqueRecords.addAll(records);

        System.out.println(uniqueRecords.size());

        JDBCDriver jdbcDriver = new JDBCDriver(1);
        jdbcDriver.dropTable();
        jdbcDriver.createTable();
        jdbcDriver.writeRecords(records);
    }
}
