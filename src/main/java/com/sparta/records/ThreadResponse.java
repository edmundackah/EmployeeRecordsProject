package com.sparta.records;

public class ThreadResponse implements Comparable {
    private int id, records;
    private long timeTaken;

    public ThreadResponse(int id, int records, long timeTaken) {
        this.id = id;
        this.records = records;
        this.timeTaken = timeTaken;
    }

    @Override
    public String toString() {
        return "Thread " + id + " took "
                + timeTaken + "ms to process "
                + records + " records\n";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    @Override
    public int compareTo(Object o) {
        return this.id - ((ThreadResponse) o).id;
    }
}
