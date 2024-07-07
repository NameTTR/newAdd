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

    /**
     * 根据提醒方式，加提醒时分秒
     *
     * @param taskTime
     * @param remindByTime
     * @return
     */
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

    /**
     * 根据提醒方式，减提醒日期
     *
     * @param taskDate
     * @param remindByDate
     * @return
     */
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

    /**
     * 根据提醒方式，减提醒时分秒
     *
     * @param taskTime
     * @param remindByTime
     * @return
     */
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

    /**
     * 根据提醒方式，减提醒日期
     *
     * @param taskDate
     * @param remindByDate
     * @return
     */
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

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime
     * @return
     */
    public static Date LocalDateTimeToDate(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * LocalDate转Date
     *
     * @param localDate
     * @return
     */
    public static Date LocalDateToDate(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * LocalTime转Date
     *
     * @param localTime
     * @param localDate
     * @return
     */
    public static Date LocalTimeToDate(LocalTime localTime, LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * Date转LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime DateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    /**
     * Date转LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate DateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;

    }

    /**
     * Date转LocalTime
     *
     * @param date
     * @return
     */
    public static LocalTime DateToLocalTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }


    /**
     * 计算任务按时执行的时间
     *
     * @param fireDateTime
     * @param remindByTime
     * @param remindByDate
     * @return
     */
    public static LocalDateTime calculateNowTaskDateTime(LocalDateTime fireDateTime, Integer remindByTime, Integer remindByDate) {
        LocalDateTime nextExecutionDateTime = fireDateTime;

        if (remindByTime != null) {
            switch (remindByTime) {
                case 0:
                    break; // 无
                case 1:
                    break; // 准时
                case 2:
                    nextExecutionDateTime = nextExecutionDateTime.plusMinutes(5);
                    break;
                case 3:
                    nextExecutionDateTime = nextExecutionDateTime.plusMinutes(30);
                    break;
                case 4:
                    nextExecutionDateTime = nextExecutionDateTime.plusDays(1);
                    break;
                default:
                    break;
            }
        }

        if (remindByDate != null) {
            switch (remindByDate) {
                case 0:
                    break; // 无
                case 1:
                    break; // 当天
                case 2:
                    nextExecutionDateTime = nextExecutionDateTime.plusDays(1);
                    break;
                case 3:
                    nextExecutionDateTime = nextExecutionDateTime.plusDays(2);
                    break;
                case 4:
                    nextExecutionDateTime = nextExecutionDateTime.plusDays(3);
                    break;
                default:
                    break;
            }
        }

        return nextExecutionDateTime;
    }

    /**
     * 计算任务按时执行的时间
     *
     * @param fireDateTime
     * @param remindByTime
     * @param remindByDate
     * @return
     */
    public static LocalDateTime calculateExecuteTaskDateTime(LocalDateTime fireDateTime, Integer remindByTime, Integer remindByDate) {
        LocalDateTime nextExecutionDateTime = fireDateTime;

        if (remindByTime != null) {
            switch (remindByTime) {
                case 0:
                    break; // 无
                case 1:
                    break; // 准时
                case 2:
                    nextExecutionDateTime = nextExecutionDateTime.minusMinutes(5);
                    break;
                case 3:
                    nextExecutionDateTime = nextExecutionDateTime.minusMinutes(30);
                    break;
                case 4:
                    nextExecutionDateTime = nextExecutionDateTime.minusDays(1);
                    break;
                default:
                    break;
            }
        }

        if (remindByDate != null) {
            switch (remindByDate) {
                case 0:
                    break; // 无
                case 1:
                    break; // 当天
                case 2:
                    nextExecutionDateTime = nextExecutionDateTime.minusDays(1);
                    break;
                case 3:
                    nextExecutionDateTime = nextExecutionDateTime.minusDays(2);
                    break;
                case 4:
                    nextExecutionDateTime = nextExecutionDateTime.minusDays(3);
                    break;
                default:
                    break;
            }
        }

        return nextExecutionDateTime;
    }
}