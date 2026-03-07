package com.college.academix.util;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing roll numbers.
 * Roll number format: YYDDNNN
 * YY = Last two digits of joining year (e.g., 24 → 2024)
 * DD = Department code (AM, AD, CS, IT)
 * NNN = Student number (e.g., 031)
 *
 * Example: 24AM031
 * → Joining Year: 2024
 * → Department: Artificial Intelligence & Machine Learning
 * → Student Number: 31
 * → Current Year Level: CurrentYear - 2024
 */
public class RollNumberParser {

    private static final Pattern ROLL_NUMBER_PATTERN = Pattern.compile("^(\\d{2})([A-Za-z]+)(\\d+)$");

    private static final Map<String, String> DEPARTMENT_MAP = new HashMap<>();

    static {
        DEPARTMENT_MAP.put("AM", "Artificial Intelligence & Machine Learning");
        DEPARTMENT_MAP.put("AD", "Data Science");
        DEPARTMENT_MAP.put("CS", "Computer Science");
        DEPARTMENT_MAP.put("IT", "Information Technology");
        DEPARTMENT_MAP.put("EC", "Electronics & Communication");
        DEPARTMENT_MAP.put("EE", "Electrical Engineering");
        DEPARTMENT_MAP.put("ME", "Mechanical Engineering");
        DEPARTMENT_MAP.put("CE", "Civil Engineering");
    }

    public static int parseJoiningYear(String rollNumber) {
        Matcher matcher = ROLL_NUMBER_PATTERN.matcher(rollNumber.toUpperCase());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid roll number format: " + rollNumber);
        }
        int yearSuffix = Integer.parseInt(matcher.group(1));
        return 2000 + yearSuffix;
    }

    public static String parseDepartmentCode(String rollNumber) {
        Matcher matcher = ROLL_NUMBER_PATTERN.matcher(rollNumber.toUpperCase());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid roll number format: " + rollNumber);
        }
        return matcher.group(2).toUpperCase();
    }

    public static String parseDepartmentName(String rollNumber) {
        String code = parseDepartmentCode(rollNumber);
        String name = DEPARTMENT_MAP.get(code);
        if (name == null) {
            throw new IllegalArgumentException("Unknown department code: " + code);
        }
        return name;
    }

    public static int parseStudentNumber(String rollNumber) {
        Matcher matcher = ROLL_NUMBER_PATTERN.matcher(rollNumber.toUpperCase());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid roll number format: " + rollNumber);
        }
        return Integer.parseInt(matcher.group(3));
    }

    public static int calculateCurrentYearLevel(String rollNumber) {
        int joiningYear = parseJoiningYear(rollNumber);
        int currentCalendarYear = Year.now().getValue();
        int yearLevel = currentCalendarYear - joiningYear;
        // Year level should be at least 1 (first year) and at most 4
        return Math.max(1, Math.min(yearLevel, 4));
    }

    public static boolean isValidRollNumber(String rollNumber) {
        if (rollNumber == null || rollNumber.isBlank()) {
            return false;
        }
        Matcher matcher = ROLL_NUMBER_PATTERN.matcher(rollNumber.toUpperCase());
        if (!matcher.matches()) {
            return false;
        }
        String deptCode = matcher.group(2).toUpperCase();
        return DEPARTMENT_MAP.containsKey(deptCode);
    }
}
