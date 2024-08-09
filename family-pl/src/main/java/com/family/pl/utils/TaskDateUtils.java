package com.family.pl.utils;

import com.family.common.util.ExceptionLogUtil;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>
 * 日期工具类
 * </p>
 *
 * @author 高俊炜
 * @since 2024-6-27
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
            // On time
            case 1:
                break;
            // 5 minutes early
            case 2:
                calendar.add(Calendar.MINUTE, +5);
                break;
            // 30 minutes early
            case 3:
                calendar.add(Calendar.MINUTE, +30);
                break;
            // 1 day early
            case 4:
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
            // On the same day
            case 1:
                break;
            // 1 day early
            case 2:
                calendar.add(Calendar.DAY_OF_YEAR, +1);
                break;
            // 2 days early
            case 3:
                calendar.add(Calendar.DAY_OF_YEAR, +2);
                break;
            // 3 days early
            case 4:
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
            // On time
            case 1:
                break;
            // 5 minutes early
            case 2:
                calendar.add(Calendar.MINUTE, -5);
                break;
            // 30 minutes early
            case 3:
                calendar.add(Calendar.MINUTE, -30);
                break;
            // 1 day early
            case 4:
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
            // On the same day
            case 1:
                break;
            // 1 day early
            case 2:
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            // 2 days early
            case 3:
                calendar.add(Calendar.DAY_OF_YEAR, -2);
                break;
            // 3 days early
            case 4:
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

    public static Date calculateStartTime(Date date, int remindByTime, int remindByDate) throws ParseException {

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            // 根据 remindByTime 和 remindByDate 调整时间
            if (remindByTime == 4) {
                cal.add(Calendar.DATE, -1);
            } else if (remindByTime == 3) {
                cal.add(Calendar.MINUTE, -30);
            } else if (remindByTime == 2) {
                cal.add(Calendar.MINUTE, -5);
            }

            if (remindByDate >= 1 && remindByDate <= 4) {
                cal.add(Calendar.DATE, -(remindByDate - 1));
            }

            return cal.getTime();

    }


}