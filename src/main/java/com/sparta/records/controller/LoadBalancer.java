package com.sparta.records.controller;

import com.sparta.records.models.DBWorker;
import com.sparta.records.models.Employee;
import com.sparta.records.util.ThreadResponse;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class LoadBalancer {
    private static String className = LoadBalancer.class.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);

    //get the number of threads supported by the host system
    public enum Performance { SINGLE_THREAD, MAX_PERFORMANCE, BALANCED}

    private static final int AVAILABLE_THREADS = Runtime.getRuntime().availableProcessors();
    private static int PREFERRED_THREADS;

    private List<Employee> records;
    private static List<List<Employee>> slices;

    public LoadBalancer(Performance performance, List<Employee> records) {
        switch (performance) {
            case SINGLE_THREAD -> PREFERRED_THREADS = 1;
            case BALANCED -> PREFERRED_THREADS = AVAILABLE_THREADS > 2 ? AVAILABLE_THREADS - 2 : 1;
            case MAX_PERFORMANCE -> PREFERRED_THREADS = AVAILABLE_THREADS;
        }

        this.records = records;
        logger.debug("Migration tool will use " + PREFERRED_THREADS + " threads");

        //get slices to assign to threads
        if (PREFERRED_THREADS > 1) { slices = getSlice();}
    }

    public int getPreferredThreads() { return PREFERRED_THREADS; }

    public List<ThreadResponse> createWorkers() throws InterruptedException {
        JDBCDriver jdbcDriver = new JDBCDriver(1);
        jdbcDriver.buildDBFromSchema();

        List<ThreadResponse> callbacks = new ArrayList<>();

        //thread pool created to match required threads
        ExecutorService executorService = Executors.newFixedThreadPool(PREFERRED_THREADS);
        List<DBWorker> workers = new ArrayList<>();

        if (PREFERRED_THREADS > 1) {
            logger.debug("creating " + PREFERRED_THREADS + " worker objects");
            for (int x = 0; x < PREFERRED_THREADS; x++) {
                workers.add(new DBWorker(slices.get(x), x));
            }
        } else {
            logger.debug("Running in single threaded mode");
            //callbacks.add(jdbcDriver.writeRecords(list));
            workers.add(new DBWorker(records, 0));
        }

        //start all the workers.
        logger.debug("Starting " + PREFERRED_THREADS + " threads");
        List<Future<ThreadResponse>> res = executorService.invokeAll(workers);

        //print thread result
        res.stream().forEach((r) -> {
            try {
                logger.debug("Writing thread responses into callback array");
                callbacks.add(r.get());
            } catch (InterruptedException e) {
                logger.fatal(e);
            } catch (ExecutionException e) {
                logger.fatal(e);
            }
        });
        logger.debug("returning thread responses");
        return callbacks;
    }

    //creates a series of smaller lists from the main list
    public List<List<Employee>> getSlice() {
        //object responsible for providing access to the CSV file
        //records from the csv file including duplicates but with isDuplicate flag set to 1
        //poorly formatted records are not in this list
        logger.debug("creating equal sub arrays for the worker objects");

        List<List<Employee>> slice = new ArrayList();
        final int numRecords = records.size();

        //create equal slices for each thread to process
        for (int i = 0; i < numRecords; i += (numRecords / PREFERRED_THREADS)) {
            slice.add(records.subList(i, Math.min(numRecords, (i + (numRecords / PREFERRED_THREADS)))));
        }

        //add final list items to the one before it, as it has only a handle of records in it
        List<Employee> temp = Stream.concat(
                slice.get(slice.size() - 1).stream(),
                slice.get(slice.size() - 2).stream()).toList();

        slice.set((slice.size() - 2), temp);
        //remove last element after concatenation
        slice.remove(slice.size() - 1);

        logger.debug("returning " + slice.size() + " sub lists");
        return slice;
    }
}
