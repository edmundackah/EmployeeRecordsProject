package com.sparta.records;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVReader {
    private static String className = CSVReader.class.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);

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

    public List<Employee> getAllRecordsWithDuplicateID() {
        return Stream.concat(uniqueRecords.stream(), getDuplicateRecords().stream())
                .collect(Collectors.toList());
    }

    private void parseRecordsFromCSV() {
        employees = new ArrayList<>();
        RegexValidator regexValidator = RegexValidator.getInstance();

        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            logger.debug("Reading employee records from " + path);
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
                        logger.fatal(e);
                    } catch(NumberFormatException e) {
                        logger.fatal(e);
                    } catch (EmployeeValidationException e) {
                        logger.fatal(e);
                    }
                }
                lineCount++;
            }
        } catch (IOException e) {
           logger.fatal(e);
        }

        //creating hashset
        uniqueRecords = new HashSet();

        //load records into sets
        for (int x = 0; x < employees.size(); x++) {
            Employee employee = employees.get(x);

            if (uniqueRecords.add(employee) == false) {
                logger.debug("marking record as duplicate | employeeID " + employee.getEmployeeID());
                employee.setIsDuplicate((byte) 1);
                duplicateRecords.add(employee);
            }
        }
    }

    private static java.sql.Date parseDate(String dateString) throws ParseException {
        logger.debug("Converting date into MySQL date object. date: " + dateString);
        java.util.Date dateOriginal = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
        return new java.sql.Date(dateOriginal.getTime());
    }
}
