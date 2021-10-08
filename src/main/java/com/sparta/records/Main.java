package com.sparta.records;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        /*
        CSVReader csvReader = CSVReader.getInstance("src/main/EmployeeRecordsLarge.csv");

        List<Employee> records = csvReader.getRecords();

        System.out.println(csvReader.getDuplicateRecords().size());

        JDBCDriver jdbcDriver = new JDBCDriver(1);
        ThreadResponse res = jdbcDriver.writeRecords(csvReader.getAllRecordsWithDuplicateID());

        System.out.println(res);
         */

        try {
            LoadBalancer loadBalancer = new LoadBalancer(LoadBalancer.Performance.MAX_PERFORMANCE);
            long startTime = System.nanoTime();
            //System.out.println(loadBalancer.getPreferredThreads());
            System.out.println(loadBalancer.getSlice().size());
            //loadBalancer.createWorkers();
            System.out.println("Whole operation took " + ((System.nanoTime() - startTime) / 1000000) + "ms to complete");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
