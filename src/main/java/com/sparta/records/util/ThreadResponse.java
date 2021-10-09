package com.sparta.records.util;

public class ThreadResponse implements Comparable {
    private final int id, records;
    private final long timeTaken;

    public ThreadResponse(int id, int records, long timeTaken) {
        this.id = id;
        this.records = records;
        this.timeTaken = timeTaken;
    }

    @Override
    public String toString() {
        return "Thread " + getId() + " took "
                + getTimeTaken() + "ms to process "
                + getRecords() + " records\n";
    }

    public int getId() {
        return id;
    }

    public int getRecords() {
        return records;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    @Override
    public int compareTo(Object o) {
        return this.getId() - ((ThreadResponse) o).getId();
    }
}
