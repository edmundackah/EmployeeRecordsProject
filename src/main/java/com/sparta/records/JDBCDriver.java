package com.sparta.records;

import com.sparta.records.util.ThreadResponse;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCDriver {
    private static String className = JDBCDriver.class.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);

    private int threadID;
    private static final int BATCH_SIZE = 1000;
    //login credentials should be handled with care in production application
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sparta-global";
    //private static final String CONNECTION_STRING = "jdbc:sqlite:coffee.db"; //jdbc:sqlite::memory:
    private static final String CONNECTION_STRING = "jdbc:mysql://127.0.0.1:3306/EmployeeProject";

    private static final HashMap<String, String> QUERIES = new HashMap(Map.of(
            "create database","CREATE DATABASE IF NOT EXISTS EmployeeProject;",
            "drop employees table" , "DROP TABLE IF EXISTS EmployeeRecords",
            "drop genders table", "DROP TABLE IF EXISTS Genders",
            "drop titles table", "DROP TABLE IF EXISTS Titles",
            "create title table", "CREATE TABLE IF NOT EXISTS Titles (\n" +
                    "    titleID\tINT AUTO_INCREMENT NOT NULL,\n" +
                    "    title VARCHAR(20) NOT NULL,\n" +
                    "    PRIMARY KEY(titleID)\n" +
                    ");",
            "add titles", "INSERT INTO Titles (title) VALUES ('Mr.'), ('Mrs.')," +
                    " ('Ms.'), ('Drs.'), ('Dr.'), ('Hon.'), ('Prof.'), ('Mx');",
            "create genders table", "CREATE TABLE Genders (\n" +
                    "    genderID TINYINT AUTO_INCREMENT NOT NULL,\n" +
                    "    gender VARCHAR(5) NOT NULL,\n" +
                    "    PRIMARY KEY(genderID)\n" +
                    ");",
            "add genders", "INSERT INTO Genders (gender) VALUES ('M'), ('F')",
            "create employee records table", "CREATE TABLE IF NOT EXISTS EmployeeRecords (\n" +
                    "    userID\tINT AUTO_INCREMENT NOT NULL,\n" +
                    "    employeeID\tINT NOT NULL,\n" +
                    "    titleID\tINT NOT NULL,\n" +
                    "    firstName\tVARCHAR(255) NOT NULL,\n" +
                    "    middleInitial\tVARCHAR(20) NULL,\n" +
                    "    lastName\tVARCHAR(255) NOT NULL,\n" +
                    "    genderID\tTINYINT NOT NULL,\n" +
                    "    email\tVARCHAR(255) NOT NULL,\n" +
                    "    dob\tDATE NOT NULL,\n" +
                    "    startDate\tDATE NOT NULL,\n" +
                    "    salary\tINT NOT NULL,\n" +
                    "    isDuplicate\tTINYINT NOT NULL,\n" +
                    "    PRIMARY KEY(userID),\n" +
                    "    FOREIGN KEY (titleID) REFERENCES Titles (titleID),\n" +
                    "    FOREIGN KEY (genderID) REFERENCES Genders (genderID)\n" +
                    ");",
            "add employee record", "INSERT INTO EmployeeRecords (employeeID, titleID, firstName, \n" +
                    "middleInitial, lastName, genderID, email, dob, startDate,\n" +
                    " salary, isDuplicate)\n" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?)"
    ));

    public JDBCDriver(int threadID) {
        this.threadID = threadID;
    }

    public void buildDBFromSchema() {
        logger.debug("building database");

        long startTime = System.nanoTime();
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD)) {
            Statement statement = conn.createStatement();

            //create the mysql database
            statement.execute(QUERIES.get("create database"));

            //drop the following tables if it exists in the database
            statement.execute(QUERIES.get("drop employees table"));
            statement.execute(QUERIES.get("drop genders table"));
            statement.execute(QUERIES.get("drop titles table"));

            //create new tables and insert data into it
            statement.execute(QUERIES.get("create genders table"));
            statement.execute(QUERIES.get("add genders"));
            statement.execute(QUERIES.get("create title table"));
            statement.execute(QUERIES.get("add titles"));
            statement.execute(QUERIES.get("create employee records table"));
        } catch (SQLException e) {
            logger.fatal(e);
        }
        logger.debug("Took " + timeElapsed(startTime) + "ms to build DB");
    }

    private static long timeElapsed(long startTime) { return ((System.nanoTime() - startTime) / 1000000);}

    public ThreadResponse writeRecords(List<Employee> records) {
        logger.debug("Thread " + threadID + " is writing " + records.size() + " records to db");
        int count  = 0;
        long startTime = System.nanoTime();

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false);
            PreparedStatement pst = conn.prepareStatement(QUERIES.get("add employee record"));

            //write in batches of 1,000 records
            for (int b = 0; b < records.size(); b++) {
                if (b != records.size() - 1 || b % BATCH_SIZE != 0) {
                    count++;

                    pst.setInt(1, records.get(b).getEmployeeID());
                    pst.setInt(2, records.get(b).getTitleID());
                    pst.setString(3, records.get(b).getFirstName());
                    pst.setString(4, records.get(b).getInitial());

                    pst.setString(5, records.get(b).getLastName());
                    pst.setInt(6, records.get(b).getGenderID());
                    pst.setString(7, records.get(b).getEmail());
                    pst.setDate(8, records.get(b).getDob());

                    pst.setDate(9, records.get(b).getStartDate());
                    pst.setInt(10, records.get(b).getSalary());
                    pst.setInt(11, records.get(b).getIsDuplicate());

                    pst.addBatch();
                }
                if (b == records.size() -1 || b % BATCH_SIZE == 0) {
                    logger.debug("Committing batch: " + count + " | thread " + threadID);
                    pst.executeBatch();
                    conn.commit();
                }
            }

            pst.close();
        } catch (SQLException e) {
            logger.fatal(e);
        }

        return new ThreadResponse(threadID, count, timeElapsed(startTime));
    }

}
