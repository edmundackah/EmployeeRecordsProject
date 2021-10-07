package com.sparta.records;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCDriver {
    private static int threadID;
    private static final int BATCH_SIZE = 1000;
    //private static final String CONNECTION_STRING = "jdbc:sqlite:coffee.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite::memory:";

    private static final HashMap<String, String> QUERIES = new HashMap(Map.of(
            "drop employees table" , "DROP TABLE IF EXISTS EmployeeRecords",
            "drop genders table", "DROP TABLE IF EXISTS Genders",
            "drop titles table", "DROP TABLE IF EXISTS Titles",
            "create title table", "CREATE TABLE Titles (\n" +
                    "\t\"titleID\"\tINTEGER NOT NULL,\n" +
                    "\t\"title\" TEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"titleID\" AUTOINCREMENT)\n" +
                    ");",
            "add titles", "INSERT INTO Titles (title) VALUES ('Mr.'), ('Mrs.')," +
                    " ('Ms.'), ('Drs.'), ('Dr.'), ('Hon.'), ('Prof.'), ('Mx');",
            "create genders table", "CREATE TABLE Genders (\n" +
                    "\t\"genderID\" INTEGER NOT NULL,\n" +
                    "\t\"gender\" VARCHAR(1) NOT NULL,\n" +
                    "\tPRIMARY KEY(\"genderID\" AUTOINCREMENT)\n" +
                    ");",
            "add genders", "INSERT INTO Genders (gender) VALUES (\"M\"), (\"F\")",
            "create employee records table", "CREATE TABLE IF NOT EXISTS EmployeeRecords (\n" +
                    "\t\"userID\"\tINTEGER NOT NULL,\n" +
                    "\t\"employeeID\"\tINTEGER NOT NULL,\n" +
                    "\t\"titleID\"\tTEXT NOT NULL,\n" +
                    "\t\"firstName\"\tTEXT NOT NULL,\n" +
                    "\t\"middleInitial\"\tTEXT,\n" +
                    "\t\"lastName\"\tTEXT NOT NULL,\n" +
                    "\t\"genderID\"\tVARCHAR(1) NOT NULL,\n" +
                    "\t\"email\"\tTEXT,\n" +
                    "\t\"dob\"\tDATE NOT NULL,\n" +
                    "\t\"startDate\"\tDATE NOT NULL,\n" +
                    "\t\"salary\"\tINTEGER NOT NULL,\n" +
                    "\t\"isDuplicate\"\tINTEGER NOT NULL,\n" +
                    "\tPRIMARY KEY(\"userID\" AUTOINCREMENT),\n" +
                    "\tFOREIGN KEY (titleID) REFERENCES Titles (titleID),\n" +
                    "\tFOREIGN KEY (genderID) REFERENCES Genders (genderID)\n" +
                    ");",
            "add employee record", "INSERT INTO EmployeeRecords (employeeID, titleID, firstName, \n" +
                    "middleInitial, lastName, genderID, email, dob, startDate,\n" +
                    " salary, isDuplicate)\n" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?)"
    ));

    //TODO: Add date type to database

    public JDBCDriver(int threadID) {
        this.threadID = threadID;
        buildDBFromSchema();
    }

    private static void buildDBFromSchema() {
        long startTime = System.nanoTime();
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Statement statement = conn.createStatement();

            //drop the following tables if it exists in the database
            statement.execute(QUERIES.get("drop employees table"));
            statement.execute(QUERIES.get("drop genders table"));
            statement.execute(QUERIES.get("drop titles table"));

            //create new tables and insert data into it
            statement.execute(QUERIES.get("create title table"));
            statement.execute(QUERIES.get("add titles"));
            statement.execute(QUERIES.get("create genders table"));
            statement.execute(QUERIES.get("add genders"));
            statement.execute(QUERIES.get("create employee records table"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Took " + timeElapsed(startTime) + "ms to build DB");
    }

    private static long timeElapsed(long startTime) {
        return ((System.nanoTime() - startTime) / 1000000);
    }

    public ThreadResponse writeRecords(List<Employee> records) {
        int count  = 0;
        long startTime = System.nanoTime();

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
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
                    System.out.println("\nCommitting batch: " + count);
                    pst.executeBatch();
                    conn.commit();
                }
            }

            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ThreadResponse(threadID, count, timeElapsed(startTime));
    }

}
