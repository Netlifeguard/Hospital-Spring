package com.nie.admin.tools;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateAndTimeUtils {
    public static String getPastDate(int n) {
        LocalDate date = LocalDate.of(2023, 10, 1);
        LocalDate localDate = date.minusDays(n);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(timeFormatter);
    }

    public static String getNowDate() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
    }
}
