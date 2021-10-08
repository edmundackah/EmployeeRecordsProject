CREATE DATABASE IF NOT EXISTS EmployeeProject;

USE EmployeeProject;

DROP TABLE IF EXISTS Titles;
DROP TABLE IF EXISTS Genders;
DROP TABLE IF EXISTS EmployeeRecords;

CREATE TABLE IF NOT EXISTS Titles (
    titleID	INT AUTO_INCREMENT NOT NULL,
    title VARCHAR(20) NOT NULL,
    PRIMARY KEY(titleID)
);

CREATE TABLE Genders (
    genderID TINYINT AUTO_INCREMENT NOT NULL,
    gender TINYINT NOT NULL,
    PRIMARY KEY(genderID)
);

CREATE TABLE IF NOT EXISTS EmployeeRecords (
    userID	INT AUTO_INCREMENT NOT NULL,
    employeeID	INT NOT NULL,
    titleID	INT NOT NULL,
    firstName	VARCHAR(255) NOT NULL,
    middleInitial	VARCHAR(20) NULL,
    lastName	VARCHAR(255) NOT NULL,
    genderID	TINYINT NOT NULL,
    email	VARCHAR(255) NOT NULL,
    dob	DATE NOT NULL,
    startDate	DATE NOT NULL,
    salary	INT NOT NULL,
    isDuplicate	TINYINT NOT NULL,
    PRIMARY KEY(userID),
    FOREIGN KEY (titleID) REFERENCES Titles (titleID),
    FOREIGN KEY (genderID) REFERENCES Genders (genderID)
);

