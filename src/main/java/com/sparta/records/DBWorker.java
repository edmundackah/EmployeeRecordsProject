package com.sparta.records;

import java.util.List;
import java.util.concurrent.Callable;

public class DBWorker implements Callable {
    private List<Employee> records;
    private int threadID;

    public DBWorker (List<Employee> records, int threadID) {
        this.records = records;
        this.threadID = threadID;
    }

    @Override
    public ThreadResponse call() throws Exception {
        //code here runs in a thread
        JDBCDriver jdbcDriver = new JDBCDriver(threadID);
        return jdbcDriver.writeRecords(records);
    }
}
