package com.sparta.records;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LoadBalancer {
    //get the number of threads supported by the host system
    private static final int AVAILABLE_THREADS = Runtime.getRuntime().availableProcessors();
    //object responsible for providing access to the CSV file
    private static final CSVReader RECORDS = CSVReader.getInstance("src/main/EmployeeRecordsLarge.csv");

    public void distributeJobs() {

    }


    //creates a series of smaller lists from the main list
    private List<List<Employee>> getSlice() {
        //records from the csv file including duplicates but with is Duplicate flag
        //poorly formatted records are not in this list
        List<Employee> list = RECORDS.getAllRecordsWithDuplicateID();

        List<List<Employee>> slice = new ArrayList();
        final int numRecords = list.size();

        //create equal slices for each thread to process
        for (int i = 0; i < numRecords; i += AVAILABLE_THREADS) {
            slice.add(list.subList(i, Math.min(numRecords, i + AVAILABLE_THREADS)));
        }

       return slice;
    }




}
