package com.ohadshai.savta.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents utilities for validation on input fields.
 */
public class ValidationUtils {

    /**
     * Checks whether the specified email address format is valid or not.
     *
     * @param email The email address to check.
     * @return Returns true if the email address format is valid, otherwise false.
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Checks whether the specified full name format is valid or not.
     *
     * @param fullName The full name to check.
     * @return Returns true if the full name format is valid, otherwise false.
     */
    public static boolean isFullNameValid(String fullName) {
        return fullName.trim().contains(" ");
    }

}
