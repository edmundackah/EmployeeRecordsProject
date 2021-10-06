package com.sparta.records;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Person {
    private String title, firstName, lastName, email;
    private char initial, gender;
    private Date dob;

    public Person(String title, String firstName, String lastName,
                  String email, char initial, char gender, String dob) throws ParseException {
        this.title = title == null ? "Mx" : title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.initial = initial;
        this.gender = gender;
        this.dob = parseDate(dob);
    }

    public static Date parseDate(String dateString) throws ParseException {
        java.util.Date dob = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
        return new java.sql.Date(dob.getTime());
    }

    @Override
    public String toString() {
        return "Person{" +
                "title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", initial=" + initial +
                ", gender=" + gender +
                ", dob=" + dob +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public char getInitial() {
        return initial;
    }

    public char getGender() {
        return gender;
    }

    public Date getDob() {
        return dob;
    }
}
