package com.nju.banxing.demo.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.enums.DayOfWeekEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author: jaggerw
 * @Description: 日期工具类
 * @Date: 2020/11/4
 */
public class DateUtil {

    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前时间
     * @return
     */
    public static LocalDateTime now(){
        return LocalDateTime.now();
    }

    /**
     * 获取当前时间string类型
     * @return
     */
    public static String now2str(){
        return LocalDateTime.now().format(dtf);
    }

    /**
     * 解析时间
     * @param str
     * @return
     */
    public static LocalDateTime str2Date(String str){
        return LocalDateTime.parse(str,dtf);
    }

    /**
     * 比较时间大小
     * @param date1
     * @param date2
     * @return
     */
    public static int compare(LocalDateTime date1, LocalDateTime date2){
        return date1.compareTo(date2);
    }


    /**
     * 判断是否在时间范围内
     * @param targetTime
     * @param rangeStart
     * @param rangeEnd
     * @return
     */
    public static boolean isIncluded(LocalDateTime targetTime,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd){
        return isIncluded(targetTime,targetTime,rangeStart,rangeEnd);
    }

    public static boolean isIncluded(LocalDateTime targetStart, LocalDateTime targetEnd,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd){
        return (targetStart.isBefore(targetEnd) || targetStart.isEqual(targetEnd))
                && targetStart.isAfter(rangeStart)
                && targetEnd.isBefore(rangeEnd);
    }

    /**
     * 获取接下来两周时间
     * @return
     */
    public static List<Map<LocalDateTime, DayOfWeekEnum>> getNextTwoWeeks(){
        ArrayList<Map<LocalDateTime,DayOfWeekEnum>> list = Lists.newArrayList();
        LocalDateTime now = LocalDateTime.now();
        for(int i =0;i<14;++i){
            int value = now.getDayOfWeek().getValue();
            DayOfWeekEnum day = DayOfWeekEnum.getEnumByCode(value);
            HashMap<LocalDateTime, DayOfWeekEnum> map = Maps.newHashMap();
            map.put(now,day);
            list.add(map);
            now = now.plusDays(1);
        }
        return list;
    }

    /**
     * 获取第二天日期
     * @return
     */
    public static LocalDateTime getNextDay(){
        return LocalDateTime.now().plusDays(1);
    }

    public static LocalDateTime getNextDay(LocalDateTime dateTime){
        return dateTime.plusDays(1);
    }


}
