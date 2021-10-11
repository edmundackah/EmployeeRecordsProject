# CSV Data Migration Project

This project was designed to demonstrate the use of JDBC to connect to a database and and the query the database. Moreover, this artefact shows how data can be read from a CSV file and sanitised. Moreover, concurrency was used in the data migration process to reduce the time required to write over 65,00 records

# Table of Contents
1. [Migration Tool](#migration_tool)
2. [Logging](#logging)
3. [Further Works](#3further_work)
4. [Setup Guide](#setup_guide)
5. [Acknowledgement](#acknowledgements)

## Features

- [x]  Regex Validation
- [x]  Custom Exceptions
- [x]  Dynamic Thread Scaling
- [x]  Multi-Threaded
- [x]  Containerised Database
- [x]  MySQL support
- [x]  Use of Functional Programming

- [x]  MVC Design Pattern
- [x]  Maven dependency management
- [x]  SOLID Principles applied
- [x]  Logging
- [x]  GUI

## 1. Migration Application <a name="migration_tool"></a>

The data migration process is started by running the `JavaFxApp` class in the view package. Once the GUI has launched, the user can click on the run migration button to start reading the CSV files.

![Screenshot 2021-10-11 003647.png](images/Screenshot_2021-10-11_003647.png)

As migrating a vast amount of data from a CSV file into a database is a time consuming activity, the migration application is equipped with three performance profiles to help in addressing this problem.

```java
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
```

At runtime the system will query the operating system to find out how many threads the host CPU supports.  If the performance profile is set to balance, provided the system has more than 2 threads available, the migration tool will use the available threads - 2 for data migration. This done to not hinder the database server and host OS. Performance mode is a more aggressive mode that uses all available thread in order to maximise performance.

![Screenshot 2021-10-11 003821.png](images/Screenshot%202021-10-11%20003821.png)

Using 12 threads, the database was sanitised and migrated to MySQL in under 10 seconds on core i7 mobile processors.

![Screenshot 2021-10-11 011421.png](images/Screenshot%202021-10-11%20011421.png)

Sanitised records from the CSV

## 2. Logging <a name="logging"></a>

All good applications need a robust set of logs to aid debugging. The system logs tab of the GUI shows all the logs recorded by the application. The logs can be sorted by severity, time and source package to name a few.

![Screenshot 2021-10-11 011546.png](images/Screenshot%202021-10-11%20011546.png)

## 3. Further Work <a name="further_work"></a>

Complications experienced in the development of this artefact took more time to address than initially anticipated, that and rapidly approaching deadlines meant some features had to be shelved in order to ensure a working artefact could be delivered on time. In the near future I would like to implement  the following features.

- [ ]  More Unit Test
- [ ]  Asynchronous event handlers
- [ ]  Better implementation of the DAO pattern

## 4. Setup Guide <a name="setup_guide"></a>

To setup a MySQL [Docker](https://hub.docker.com/_/mysql) container can be setup using the attached link.
Alternatively the connection string the `JDBCDriver` class can be modified to use an existing MySQL server connection.

***Warning Docker requires Hyper-V support on Host OS.***

Running the `JavaFxApp` will produce the migration application.

## 5. Acknowledgements <a name="acknowledgements"></a>

- Want to take moment to thank my trainers Neil and Cathy for the help and support in putting this project together.
- Would also like to thank my colleagues in Engineering 95 & 96 for all the debugging assistance.