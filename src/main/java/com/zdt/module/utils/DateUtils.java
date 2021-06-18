package com.zdt.module.utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @version: 1.00.00
 * @description: 日期工具类
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-18 9:15
 */
public class DateUtils {

    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDD = "yyyy-MM-dd";

    /**
     * date转换为string
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String convertDate(Date date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = dateToDateTime(date);
        return formatter.format(localDateTime);
    }

    /**
     * date转换LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToDateTime(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * date转换LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    /**
     * LocalDate 转换 date
     *
     * @param localDate
     * @return
     */
    public static Date localToDate(LocalDate localDate) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * date 转换 LocalTime
     *
     * @param date
     * @return
     */
    public static LocalTime dateToLocalTime(Date date) {
        Instant instant = date.toInstant();
        LocalTime localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime();
        return localTime;
    }

    /**
     * 字符串转换为日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date getDate(String date, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
        return dateTimeToDate(localDateTime);
    }

    /**
     * LocalDateTime 转换为date
     *
     * @param localDateTime
     * @return
     */
    public static Date dateTimeToDate(LocalDateTime localDateTime) {
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * localDateTime 转换为String
     *
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(localDateTime);
    }

    /**
     * String 转换 localDateTime
     *
     * @param date
     * @param pattern
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String date, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
        return localDateTime;
    }

    /**

     获取当前时间之后的某一天的最小时间
     @param date
     @return
     */
    public static Date afterXDateTimeMIN(Date date, int after) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.plusDays(after);
        localDateTime = localDateTime.with(LocalTime.MIN);
        return dateTimeToDate(localDateTime);
    }
    /**

     获取当前时间之后的某一天的最大时间
     @param date
     @return
     */
    public static Date afterXDateTimeMAX(Date date, int after) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.plusDays(after);
        localDateTime = localDateTime.with(LocalTime.MAX);
        return dateTimeToDate(localDateTime);
    }
    /**

     获取当前时间之前的某一天的最小时间
     @param date
     @return
     */
    public static Date beforeXDateTimeMIN(Date date, int before) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.minusDays(before);
        localDateTime = localDateTime.with(LocalTime.MIN);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    /**

     获取当前时间之前的某一天的最大时间
     @param date
     @return
     */
    public static Date beforeXDateTimeMAX(Date date, int before) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.minusDays(before);
        localDateTime = localDateTime.with(LocalTime.MAX);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    /**

     获取本月的第一天 00:00:00
     @return
     */
    public static Date currentFirstDayOfMonth() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        return dateTimeToDate(localDateTime);
    }
    /**

     获取传入时间月份的最后一天
     @return
     */
    public static Date currentXDayOfMonth(Date date) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        return dateTimeToDate(localDateTime);
    }
    /**

     所选date的月份的第一天
     @return
     */
    public static Date beforeXFirstDayOfMonth(Date date) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        return dateTimeToDate(localDateTime);
    }
    /**

     获取前几个月的1号0点 00:00:00
     @return
     */
    public static Date preXDayOfMonthMIN(int month) {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.minusMonths(month);
        localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        return dateTimeToDate(localDateTime);
    }
    /**

     获取前几个月的1号0点 00:00:00
     @return
     */
    public static Date preXDayOfMonthMIN(Date date, int month) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.minusMonths(month);
        localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        return dateTimeToDate(localDateTime);
    }
    /**

     传入时间的的前几个月的时间
     @return
     */
    public static Date preXDayOfMonth(Date date, int month) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.minusMonths(month);
        return dateTimeToDate(localDateTime);
    }
    /**

     传入时间的的前几个月的时间
     @return
     */
    public static Date afterXDayOfMonth(Date date, int month) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.plusMonths(month);
        return dateTimeToDate(localDateTime);
    }
    /**

     获取前几个月的最后一天23：59：59
     @return
     */
    public static Date preXDayOfMonthMAX(int month) {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.minusMonths(month);
        localDateTime = localDateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        return dateTimeToDate(localDateTime);
    }
    /**

     获取某个时间几个月的最后一天23：59：59
     @return
     */
    public static Date preXDayOfMonthMAX(Date date, int month) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.minusMonths(month);
        localDateTime = localDateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        return dateTimeToDate(localDateTime);
    }
    /**

     两个日期相差多少个月
     @param date1
     @param date2
     @return
     */
    public static Long getUntilMonth(Date date1, Date date2) {
        LocalDate localDate1 = dateToLocalDate(date1);
        LocalDate localDate2 = dateToLocalDate(date2);
        return ChronoUnit.MONTHS.between(localDate1, localDate2);
    }
    /**

     两个日期相差多少天
     @param date1
     @param date2
     @return
     */
    public static Long getUntilDay(Date date1, Date date2) {
        LocalDate localDate1 = dateToLocalDate(date1);
        LocalDate localDate2 = dateToLocalDate(date2);
        return ChronoUnit.DAYS.between(localDate1, localDate2);
    }
    /**

     两个日期相差多少小时
     @param date1
     @param date2
     @return
     */
    public static Long getUntilHours(Date date1, Date date2) {
        LocalDateTime localDate1 = dateToDateTime(date1);
        LocalDateTime localDate2 = dateToDateTime(date2);
        Long senonds = Duration.between(localDate1, localDate2).get(ChronoUnit.SECONDS);
        return senonds / 3600;
    }
    /**

     两个日期相差多少小时 double 约等于
     @param date1
     @param date2
     @return
     */
    public static double getUntilHoursByDouble(Date date1, Date date2) {
        LocalDateTime localDate1 = dateToDateTime(date1);
        LocalDateTime localDate2 = dateToDateTime(date2);
        Long seconds = Duration.between(localDate1, localDate2).get(ChronoUnit.SECONDS);
        BigDecimal secondss = BigDecimal.valueOf(seconds);
        BigDecimal hours = secondss.divide(BigDecimal.valueOf(3600), 2, BigDecimal.ROUND_HALF_UP);
        return hours.doubleValue();
    }
    /**

     两个日期相差多少秒
     @param date1
     @param date2
     @return
     */
    public static Long getUntilSecond(Date date1, Date date2) {
        LocalDateTime localDate1 = dateToDateTime(date1);
        LocalDateTime localDate2 = dateToDateTime(date2);
        return Duration.between(localDate1, localDate2).get(ChronoUnit.SECONDS);
    }

    /**

     当前时间23：59：59
     @param date
     @return
     */
    public static Date currentMax(Date date) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.with(LocalTime.MAX);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    /**

     当前时间00:00:00
     @param date
     @return
     */
    public static Date currentMin(Date date) {
        LocalDateTime localDateTime = dateToDateTime(date);
        localDateTime = localDateTime.with(LocalTime.MIN);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
