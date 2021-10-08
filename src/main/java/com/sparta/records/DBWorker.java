package com.sparta.records;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class DBWorker implements Callable<ThreadResponse> {
    private static String className = DBWorker.class.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);

    private List<Employee> records;
    private int threadID;

    public DBWorker (List<Employee> records, int threadID) {
        this.records = records;
        this.threadID = threadID;
    }

    @Override
    public ThreadResponse call() throws Exception {
        JDBCDriver jdbcDriver = new JDBCDriver(threadID);
        return jdbcDriver.writeRecords(records);
    }
}
