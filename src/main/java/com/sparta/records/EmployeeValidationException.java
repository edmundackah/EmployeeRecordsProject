package com.sparta.records;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeValidationException extends Exception {
    private static final HashMap<Integer, String> VALIDATION_ERR_MESSAGES = new HashMap(Map.of(
            0,"Employee ID shouldn't be less than 1",
            1,"Title doesn't satisfy Regex expression",
            2,"First Name doesn't satisfy Regex expression",
            3,"Middle initial doesn't satisfy Regex expression",
            4,"Surname doesn't satisfy Regex expression",
            5,"Gender doesn't satisfy Regex expression",
            6,"E-mail doesn't satisfy Regex expression",
            7,"Date of Birth is the wrong format",
            8,"Start date is the wrong format",
            9,"Salary must be greater than 0. Pretty sure that's Illegal"
    ));

    public EmployeeValidationException(List<Boolean> regexResults) {
        super(buildMessage(regexResults));
    }

    private static String buildMessage(List<Boolean> regexResults) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Regex validation flagged the following issues:");

        for (int i = 0; i < regexResults.size(); i++) {
            if (regexResults.get(i) == false) {
                stringBuilder.append("\n\t" + VALIDATION_ERR_MESSAGES.get(i));
            }
        }
        return  stringBuilder.toString();
    }
}
