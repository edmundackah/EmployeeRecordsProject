package com.sparta.records.models;

import java.sql.Date;
import java.text.ParseException;

public class Person {
    private String title, firstName, lastName, email;
    private char initial;
    private char gender;
    private Date dob;

    public Person(String title, String firstName, String lastName,
                  String email, char initial, char gender, Date dob) throws ParseException {
        this.title = title == null ? "Mx" : title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.initial = initial;
        this.gender = gender;
        this.dob = dob;
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

    public String getTitle() {return title;}

    public int getTitleID() {
        switch (title) {
            case "Mr":
                return 1;
            case "Mrs.":
                return 2;
            case "Ms.":
                return 3;
            case "Drs.":
                return 4;
            case "Dr.":
                return 5;
            case "Hon.":
                return 6;
            case "Prof.":
                return 7;
            default:
                return 8;
        }
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

    public String getInitial() {
        return Character.toString(initial);
    }

    public String getGender() {return Character.toString(gender);}

    public int getGenderID() {return gender == 'F' ? 2 : 1;}

    public Date getDob() {
        return dob;
    }
}
