package com.sparta.records;

import java.sql.Date;
import java.text.ParseException;
import java.util.Objects;

public class Employee extends Person {
    private int employeeID;
    private int salary = 0;
    private byte isDuplicate = 0;
    private Date startDate;

    public Employee(String title, String firstName, String lastName, String email, char initial,
                    char gender, String dob, int employeeID, int salary, String startDate) throws ParseException {
        super(title, firstName, lastName, email, initial, gender, dob);
        this.employeeID = employeeID;
        this.salary = salary;
        this.startDate = parseDate(startDate);
    }

    public void setIsDuplicate(byte isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    public byte getIsDuplicate() {
        return isDuplicate;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeID=" + employeeID +
                ", salary=" + salary +
                ", startDate=" + startDate +
                ", super{" + super.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Employee)) return false;

        //two employees are equal if they have the same employee ID
        if (((Employee) o).employeeID == this.getEmployeeID()) {
            return true;
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployeeID());
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public int getSalary() {
        return salary;
    }

    public Date getStartDate() {
        return startDate;
    }
}

