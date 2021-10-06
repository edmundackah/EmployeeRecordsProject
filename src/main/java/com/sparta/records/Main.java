package com.sparta.records;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        CSVReader csvReader = CSVReader.getInstance();

        List<Employee> records = csvReader.getEmployeeRecords("src/main/EmployeeRecords.csv");

        System.out.println(records.size());

        //get only unique records
        Set<Employee> uniqueRecords = new HashSet<>();
        uniqueRecords.addAll(records);

        System.out.println(uniqueRecords.size());
    }
}
