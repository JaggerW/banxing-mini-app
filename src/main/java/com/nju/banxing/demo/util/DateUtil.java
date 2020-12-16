package com.nju.banxing.demo.util;

import com.google.common.collect.Lists;
import com.nju.banxing.demo.enums.DayOfWeekEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author: jaggerw
 * @Description: 日期工具类
 * @Date: 2020/11/4
 */
public class DateUtil {

    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter cnMonthDay = DateTimeFormatter.ofPattern("MM月dd日");
    public static DateTimeFormatter enHourMinute = DateTimeFormatter.ofPattern("HH:mm");
    public static DateTimeFormatter wxPayFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 获取当前时间
     *
     * @return
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前时间string类型
     *
     * @return
     */
    public static String now2str() {
        return LocalDateTime.now().format(dtf);
    }

    /**
     * 解析时间
     *
     * @param str
     * @return
     */
    public static LocalDateTime str2Date(String str) {
        return LocalDateTime.parse(str, dtf);
    }

    /**
     * LocalDateTime转时间戳
     *
     * @param time
     * @return
     */
    public static long toTimeStamp(LocalDateTime time) {
        return time.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static long toTimeStamp(LocalDate time) {
        return time.atStartOfDay(ZoneOffset.of("+8")).toInstant().toEpochMilli();
    }

    public static LocalDate toLocalDate(long timeStamp){
        return Instant.ofEpochMilli(timeStamp).atZone(ZoneOffset.of("+8")).toLocalDate();
    }

    public static LocalDate toLocalDate(Date date){
        return date.toInstant().atZone(ZoneOffset.of("+8")).toLocalDate();
    }

    public static LocalTime toLocalTime(Date date){
        return date.toInstant().atZone(ZoneOffset.of("+8")).toLocalTime();
    }

    public static LocalDateTime toLocalDateTime(Date date){
        return date.toInstant().atZone(ZoneOffset.of("+8")).toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(long timeStamp){
        return Instant.ofEpochMilli(timeStamp).atZone(ZoneOffset.of("+8")).toLocalDateTime();
    }

    public static int getSecond(LocalTime time) {
        return time.toSecondOfDay();
    }

    /**
     * 比较时间大小
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int compare(LocalDateTime date1, LocalDateTime date2) {
        return date1.compareTo(date2);
    }

    public static boolean equalZero(LocalTime time) {
        return equalZero(time, time);
    }

    public static boolean equalZero(LocalTime time1, LocalTime time2) {
        LocalTime zero = LocalTime.of(0, 0);
        return time1.equals(zero) && time2.equals(zero);
    }

    /**
     * 判断是否在时间范围内
     *
     * @param sourceTime
     * @param rangeStart
     * @param rangeEnd
     * @return
     */
    public static boolean isIncluded(LocalTime sourceTime,
                                     LocalTime rangeStart, LocalTime rangeEnd) {
        return isIncluded(sourceTime, sourceTime, rangeStart, rangeEnd);
    }

    public static boolean isIncluded(LocalTime sourceStart, LocalTime sourceEnd,
                                     LocalTime rangeStart, LocalTime rangeEnd) {
        return (sourceStart.isBefore(sourceEnd) || sourceStart.equals(sourceEnd))
                && sourceStart.isAfter(rangeStart)
                && sourceEnd.isBefore(rangeEnd);
    }

    /**
     * 获取接下来两周时间
     *
     * @return
     */
    public static List<LocalDateTime> getNextTwoWeeks() {
        ArrayList<LocalDateTime> list = Lists.newArrayList();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 14; ++i) {
            list.add(now);
            now = now.plusDays(1);
        }
        return list;
    }

    /**
     * 获取接下来最近的一个周几
     *
     * @param weekDay
     * @return
     */
    public static LocalDateTime getNextDayOfWeek(int weekDay) {
        if (weekDay < 1 || weekDay > 7) {
            throw new GlobalException(CodeMsg.ERROR_DATE);
        }
        LocalDateTime res = LocalDateTime.now();
        while (res.getDayOfWeek().getValue() != weekDay) {
            res = res.plusDays(1);
        }
        return res;
    }

    /**
     * 获取接下来两周的某天名字
     *
     * @param time
     * @return
     */
    public static String getNameDayOfWeek(LocalDateTime time) {
        int weekDay = time.getDayOfWeek().getValue();
        String desc = Objects.requireNonNull(DayOfWeekEnum.getEnumByCode(weekDay)).getDesc();
        String prefix;
        LocalDateTime nextMonday = getNextMonday();
        if (time.isBefore(nextMonday)) {
            prefix = "本";
        } else if (time.isBefore(nextMonday.plusDays(7))) {
            prefix = "下";
        } else {
            prefix = "下下";
        }
        return prefix + desc;
    }

    public static LocalDateTime getNextMonday() {
        LocalDateTime now = LocalDateTime.now();
        return getNextMonday(now);
    }

    public static LocalDateTime getNextMonday(LocalDateTime time) {
        return time.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(7);
    }

    /**
     * 获取第二天日期
     *
     * @return
     */
    public static LocalDateTime getNextDay() {
        return LocalDateTime.now().plusDays(1);
    }

    public static LocalDateTime getNextDay(LocalDateTime dateTime) {
        return dateTime.plusDays(1);
    }


}
