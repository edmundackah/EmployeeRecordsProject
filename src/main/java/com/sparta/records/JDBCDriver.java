package com.sparta.records;

import java.sql.Connection;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class JDBCDriver {
    private Connection conn = null;
    int threadID;

    public JDBCDriver(int threadID) {
        this.threadID = threadID;
    }

    private void makeConnection() {
        try {
            if (conn == null) {
                conn = DriverManager.getConnection("jdbc:sqlite::memory:");
                //conn = DriverManager.getConnection("jdbc:sqlite:coffee.db");

                conn.setAutoCommit(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void dropTable() {
        Statement statement = null;
        try {
            makeConnection();

            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("DROP TABLE EmployeeRecords");

            conn.commit();

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void createTable() {
        Statement statement = null;
        try {
            makeConnection();

            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("CREATE TABLE IF NOT EXISTS \"EmployeeRecords\" (\n" +
                    "\t\"userID\"\tINTEGER NOT NULL,\n" +
                    "\t\"employeeID\"\tINTEGER NOT NULL,\n" +
                    "\t\"title\"\tTEXT NOT NULL,\n" +
                    "\t\"firstName\"\tTEXT NOT NULL,\n" +
                    "\t\"middleInitial\"\tTEXT,\n" +
                    "\t\"lastName\"\tTEXT NOT NULL,\n" +
                    "\t\"gender\"\tTEXT NOT NULL,\n" +
                    "\t\"email\"\tTEXT,\n" +
                    "\t\"dob\"\tREAL NOT NULL,\n" +
                    "\t\"startDate\"\tREAL NOT NULL,\n" +
                    "\t\"salary\"\tINTEGER NOT NULL,\n" +
                    "\t\"isDuplicate\"\tINTEGER NOT NULL,\n" +
                    "\tPRIMARY KEY(\"userID\" AUTOINCREMENT)\n" +
                    ");");

            conn.commit();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeRecords(List<Employee> records) {
        int count  = 0;
        long startTime = System.nanoTime();

        try {
            makeConnection();
            PreparedStatement pst = conn.prepareStatement("INSERT INTO EmployeeRecords (employeeID, title, firstName, \n" +
                    "middleInitial, lastName, gender, email, dob, startDate,\n" +
                    " salary, isDuplicate)\n" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?)");

            //write in batches of 1,000 records
            for (int b = 0; b < records.size(); b++) {
                if (b != records.size() - 1 || b % 1000 != 0) {
                    count++;

                    pst.setInt(1, records.get(b).getEmployeeID());
                    pst.setString(2, records.get(b).getTitle());
                    pst.setString(3, records.get(b).getFirstName());
                    pst.setString(4, records.get(b).getInitial());
                    pst.setString(5, records.get(b).getLastName());
                    pst.setString(6, records.get(b).getGender());
                    pst.setString(7, records.get(b).getEmail());
                    pst.setDate(8, records.get(b).getDob());
                    pst.setDate(9, records.get(b).getStartDate());
                    pst.setInt(10, records.get(b).getSalary());
                    pst.setInt(11, records.get(b).getIsDuplicate());

                    pst.addBatch();
                }

                if (b == records.size() - 1 || (b % 1000) == 0) {
                    //int[] updateCounts = pst.executeBatch();
                    //System.out.println("\n" + Arrays.toString(updateCounts));
                    System.out.println("\nCommitting batch: " + count);
                    conn.commit();
                }
            }

            pst.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Thread " + threadID + " wrote " + count + " records to the database in ");
            System.out.println("Thread " + threadID + " took " + ((System.nanoTime() - startTime) / 1000000) + " ms");
        }
    }

}
