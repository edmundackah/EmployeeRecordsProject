package com.sparta.records;

import java.util.*;
import java.util.concurrent.*;

public class LoadBalancer {
    //get the number of threads supported by the host system
    public enum Performance { SINGLE_THREAD, MAX_PERFORMANCE, BALANCED}

    private static final int AVAILABLE_THREADS = Runtime.getRuntime().availableProcessors();
    private static int PREFERRED_THREADS;

    private static List<List<Employee>> slices;

    public LoadBalancer(Performance performance) {
        switch (performance) {
            case SINGLE_THREAD -> PREFERRED_THREADS = 1;
            case BALANCED -> PREFERRED_THREADS = AVAILABLE_THREADS > 2 ? AVAILABLE_THREADS - 2 : 1;
            case MAX_PERFORMANCE -> PREFERRED_THREADS = AVAILABLE_THREADS;
        }

        //get slices to assign to threads
        slices = getSlice();
    }

    public int getPreferredThreads() {
        return PREFERRED_THREADS;
    }

    public void createWorkers() throws InterruptedException {
        //TODO: System wide logging
        JDBCDriver jdbcDriver = new JDBCDriver(1);
        jdbcDriver.buildDBFromSchema();

        if (PREFERRED_THREADS > 1) {
            List<DBWorker> workers = new ArrayList<>();
            for (int x = 0; x < PREFERRED_THREADS; x++) {
                workers.add(new DBWorker(slices.get(x), x));
            }

            //thread pool created to match available processors
            ExecutorService executorService = Executors.newFixedThreadPool(PREFERRED_THREADS);
            //start all the workers.
            List<Future<ThreadResponse>> res = executorService.invokeAll(workers);

            //print thread result
            res.stream().forEach((r) -> {
                try {
                    System.out.println(r.get().toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } else {
            List<Employee> list = CSVReader
                    .getInstance("src/main/EmployeeRecordsLarge.csv")
                    .getAllRecordsWithDuplicateID();


            ThreadResponse res = jdbcDriver.writeRecords(list);

            System.out.println(res);
        }

        //spin up workers
    }

    //creates a series of smaller lists from the main list
    public List<List<Employee>> getSlice() {
        //object responsible for providing access to the CSV file
        //records from the csv file including duplicates but with isDuplicate flag set to 1
        //poorly formatted records are not in this list
        List<Employee> list = CSVReader
                .getInstance("src/main/EmployeeRecordsLarge.csv")
                .getAllRecordsWithDuplicateID();

        List<List<Employee>> slice = new ArrayList();
        final int numRecords = list.size();

        //create equal slices for each thread to process
        for (int i = 0; i < numRecords; i += (numRecords / PREFERRED_THREADS)) {
            slice.add(list.subList(i, Math.min(numRecords, (i + (numRecords / PREFERRED_THREADS)))));
        }

       return slice;
    }




}
