package com.gsvn.hrmservice.common.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String formatSalaryPeriod(Integer month, Integer year) {
        if (month == null) {
            throw new IllegalArgumentException("Month cannot be null");
        }

        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        return String.format("%d-%02d", targetYear, month);
    }

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss XXX";


    public static final String DATE_ONLY_FORMAT = "dd/MM/yyyy";

    public static String toString(OffsetDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_FORMAT));
    }

    public static String toString(OffsetDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}