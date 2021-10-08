package com.sparta.records;

import org.apache.log4j.Logger;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator {
    private static String className = RegexValidator.class.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);

    private static RegexValidator instance;

    private static final Pattern NAME_PREFIX_PATTERN = Pattern.compile("[a-zA-Z]+\\.");
    private static final Pattern NAME_PATTERN = Pattern.compile("\\p{L}+", Pattern.CASE_INSENSITIVE);
    private static final Pattern MIDDLE_INITIAL_PATTERN = Pattern.compile("[a-zA-Z.]+");
    private static final Pattern GENDER_PATTERN = Pattern.compile("[FMfm]");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)$", Pattern.CASE_INSENSITIVE);

    private RegexValidator() { }

    //synchronized - To ensure thread safety
    public synchronized static RegexValidator getInstance() {
        logger.debug("getting regex engine instance");
        if (instance == null) {
            instance = new RegexValidator();
        }
        return instance;
    }

    public boolean isValidEmployeeRecord(String[] data) throws EmployeeValidationException {
        logger.debug("Starting regex validation of employee record");
        List<Boolean> validationResults = new ArrayList();

        validationResults.add(Integer.parseInt(data[0]) > 0);

        Matcher namePrefixMatcher = NAME_PREFIX_PATTERN.matcher(data[1]);
        validationResults.add(namePrefixMatcher.find());

        String firstName = data[2];
        Matcher firstNameMatcher = NAME_PATTERN.matcher(firstName);
        validationResults.add((firstName.length() > 255 || firstNameMatcher.find()));

        String middleInitial = data[3];
        Matcher middleInitialMatcher = MIDDLE_INITIAL_PATTERN.matcher(middleInitial);
        validationResults.add((middleInitial.length() > 1 || middleInitialMatcher.find()));

        String lastName = data[4];
        Matcher lastNameMatcher = NAME_PATTERN.matcher(lastName);
        validationResults.add((lastName.length() > 256 || lastNameMatcher.find()));

        Matcher genderMatcher = GENDER_PATTERN.matcher(data[5]);
        validationResults.add((genderMatcher.find()));

        Matcher emailMatcher = EMAIL_PATTERN.matcher(data[6]);
        validationResults.add((emailMatcher.find()));

        try {
            parseAndValidateDate(data[7]);
        } catch (ParseException e) {
            logger.fatal(e);
            validationResults.add(false);
        }

        try {
            parseAndValidateDate(data[8]);
        } catch (ParseException e) {
            logger.fatal(e);
            validationResults.add(false);
        }

        validationResults.add((Integer.parseInt(data[9]) > 0));

        if (validationResults.contains(false))
            throw new EmployeeValidationException(validationResults);

        return true;
    }

    private static Date parseAndValidateDate(String dateString) throws ParseException {
        logger.debug("validating the date object and converting to the MySQL date type");
        java.util.Date dateOriginal = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
        return new java.sql.Date(dateOriginal.getTime());
    }
}
