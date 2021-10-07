package com.sparta.records;

import java.util.*;

public class LoadBalancer {
    //get the number of threads supported by the host system
    private static final int AVAILABLE_THREADS = Runtime.getRuntime().availableProcessors();

    private List<ThreadResponse> responses = new LinkedList<>();

    public static void createWorkers() {
        //TODO: thread generators and response aggregator
        //TODO: System wide logging
    }

    public List<ThreadResponse> getResponses() {
        //returns the responses from all threads after job completion
        //empty by default
        return responses;
    }

    //creates a series of smaller lists from the main list
    private List<List<Employee>> getSlice() {
        //object responsible for providing access to the CSV file
        //records from the csv file including duplicates but with isDuplicate flag set to 1
        //poorly formatted records are not in this list
        List<Employee> list = CSVReader
                .getInstance("src/main/EmployeeRecordsLarge.csv")
                .getAllRecordsWithDuplicateID();

        List<List<Employee>> slice = new ArrayList();
        final int numRecords = list.size();

        //create equal slices for each thread to process
        for (int i = 0; i < numRecords; i += AVAILABLE_THREADS) {
            slice.add(list.subList(i, Math.min(numRecords, i + AVAILABLE_THREADS)));
        }

       return slice;
    }




}
