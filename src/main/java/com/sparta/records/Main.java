package com.sparta.records;

import com.sparta.records.util.CSVReader;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

import java.util.List;

public class Main {
    private static String className = Main.class.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);
    public static void main(String[] args) {

        PropertyConfigurator.configure("log4j.properties");
        logger.debug("Starting migration application");

        List<Employee> list = CSVReader
                .getInstance("src/main/EmployeeRecordsLarge.csv")
                .getAllRecordsWithDuplicateID();

        try {
            LoadBalancer loadBalancer = new LoadBalancer(LoadBalancer.Performance.SINGLE_THREAD, list);
            long startTime = System.nanoTime();
            loadBalancer.createWorkers().stream().forEach((t) -> System.out.println(t));
            logger.debug("Whole operation took " + ((System.nanoTime() - startTime) / 1000000) + "ms to complete");
        } catch (Exception e) {
            logger.fatal(e);
        }
    }
}
