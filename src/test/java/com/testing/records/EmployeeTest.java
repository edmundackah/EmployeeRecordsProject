package com.testing.records;

import com.sparta.records.models.Employee;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeTest {

    private Date parseDate(String dateString) throws ParseException {
        java.util.Date dateOriginal = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
        return new java.sql.Date(dateOriginal.getTime());
    }

    @Test
    public void GivenNoSalary_SetSalaryToOne() throws ParseException {
        Employee employee = new Employee(
                "Mr.", "Edmund", "Ackah", "eackah@example.com",
                'J', 'M', parseDate("1998/05/10"), 455,
                -1, parseDate("2021/07/07"));

        assertEquals(1, employee.getSalary());

    }

    @Test
    public void GivenEmployeeWithNoTitle_AssignMxAsTitle() throws ParseException {
        Employee employee = new Employee(
                null, "Edmund", "Ackah", "eackah@example.com",
                'J', 'M', parseDate("1998/05/10"), 455,
                -1, parseDate("2021/07/07"));

        assertEquals("Mx", employee.getTitle());
    }

    @Test
    public void GivenStringStartDate_ReturnSQLDate() throws ParseException {
        Employee employee = new Employee(
                null, "Edmund", "Ackah", "eackah@example.com",
                'J', 'M', parseDate("1998/05/10"), 455,
                -1, parseDate("2021/07/07"));

        assertEquals(true, employee.getStartDate() instanceof Date);
    }

    @Test
    public void GivenStringDobDate_ReturnSQLDate() throws ParseException {
        Employee employee = new Employee(
                null, "Edmund", "Ackah", "eackah@example.com",
                'J', 'M', parseDate("1998/05/10"), 455,
                -1, parseDate("2021/07/07"));

        assertEquals(true, employee.getDob() instanceof Date);
    }

    @Test
    public void GivenTwoEmployeesWithSameID_TheyAreTheSame() throws ParseException {
        Employee employee = new Employee(
                null, "Edmund", "Ackah", "eackah@example.com",
                'J', 'M', parseDate("1998/05/10"), 455,
                -1, parseDate("2021/07/07"));

        Employee employee1 = new Employee(
                "Sir", "Isaac", "Newton", "eackah@example.com",
                'J', 'M', parseDate("1998/05/10"), 455,
                -1, parseDate("2021/07/07"));

        assertEquals(employee, employee1);
    }
}
