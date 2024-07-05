package com.family.pl.utils;

import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/6/27 10:42
 */
public class TaskDateUtils {

    public static Date PlusTimeReminder(Date taskTime, int remindByTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taskTime);

        switch (remindByTime) {
            case 1: // On time
                break;
            case 2: // 5 minutes early
                calendar.add(Calendar.MINUTE, +5);
                break;
            case 3: // 30 minutes early
                calendar.add(Calendar.MINUTE, +30);
                break;
            case 4: // 1 day early
                calendar.add(Calendar.DAY_OF_YEAR, +1);
                break;
            default: // No reminder
                return null;
        }

        return calendar.getTime();
    }

    public static Date PlusDateReminder(Date taskDate, int remindByDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taskDate);

        switch (remindByDate) {
            case 1: // On the same day
                break;
            case 2: // 1 day early
                calendar.add(Calendar.DAY_OF_YEAR, +1);
                break;
            case 3: // 2 days early
                calendar.add(Calendar.DAY_OF_YEAR, +2);
                break;
            case 4: // 3 days early
                calendar.add(Calendar.DAY_OF_YEAR, +3);
                break;
            default: // No reminder
                return null;
        }

        return calendar.getTime();
    }

    public static Date minusTimeReminder(Date taskTime, int remindByTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taskTime);

        switch (remindByTime) {
            case 1: // On time
                break;
            case 2: // 5 minutes early
                calendar.add(Calendar.MINUTE, -5);
                break;
            case 3: // 30 minutes early
                calendar.add(Calendar.MINUTE, -30);
                break;
            case 4: // 1 day early
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            default: // No reminder
                return null;
        }

        return calendar.getTime();
    }

    public static Date minusDateReminder(Date taskDate, int remindByDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taskDate);

        switch (remindByDate) {
            case 1: // On the same day
                break;
            case 2: // 1 day early
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case 3: // 2 days early
                calendar.add(Calendar.DAY_OF_YEAR, -2);
                break;
            case 4: // 3 days early
                calendar.add(Calendar.DAY_OF_YEAR, -3);
                break;
            default: // No reminder
                return null;
        }

        return calendar.getTime();
    }

    public static Date LocalDateTimeToDate(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        return date;
    }

    public static Date LocalDateToDate(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        return date;
    }

    public static Date LocalTimeToDate(LocalTime localTime, LocalDate localDate){
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        return date;
    }
}