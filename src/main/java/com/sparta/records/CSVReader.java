package com.sparta.records;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVReader {
    private static CSVReader instance;
    private Set<Employee> uniqueRecords;
    private List<Employee> employees;
    private List<String> badRecords = new LinkedList();
    private List<Employee> duplicateRecords = new LinkedList();

    private String path;

    private CSVReader(String path) {
        this.path = path;
        parseRecordsFromCSV();
    }

    public static CSVReader getInstance(String path) {
        if (instance == null) {
            instance = new CSVReader(path);
        }
        return instance;
    }

    public Set<Employee> getUniqueRecords() {
        return uniqueRecords;
    }

    public List<Employee> getRecords() {
        return employees;
    }

    public List<String> getBadRecords() {return badRecords;}

    public List<Employee> getDuplicateRecords() {
        return duplicateRecords;
    }

    private void parseRecordsFromCSV() {
        employees = new ArrayList<>();
        RegexValidator regexValidator = RegexValidator.getInstance();

        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String line;
            int lineCount = 1;
            while((line = bf.readLine()) != null) {
                String[] temp = line.split(",");

                //skip first line of CSV file (header info)
                if (lineCount > 1) {
                    try {
                        if (regexValidator.isValidEmployeeRecord(temp) == true) {
                            employees.add(
                                    new Employee(temp[1], temp[2], temp[4], temp[6], temp[3].charAt(0),
                                            temp[5].charAt(0), parseDate(temp[7]), Integer.parseInt(temp[0]),
                                            Integer.parseInt(temp[9]), parseDate(temp[8]))
                            );
                        } else badRecords.add(line);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch(NumberFormatException e) {
                        e.printStackTrace();
                    } catch (EmployeeValidationException e) {
                        e.printStackTrace();
                    }
                }
                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //creating hashset
        uniqueRecords = new HashSet();

        //load records into sets
        for (int x = 0; x < employees.size(); x++) {
            Employee employee = employees.get(x);

            if (uniqueRecords.add(employee) == false) {
                employee.setIsDuplicate((byte) 1);
                duplicateRecords.add(employee);
            }
        }
    }

    private static java.sql.Date parseDate(String dateString) throws ParseException {
        java.util.Date dateOriginal = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
        return new java.sql.Date(dateOriginal.getTime());
    }
}
