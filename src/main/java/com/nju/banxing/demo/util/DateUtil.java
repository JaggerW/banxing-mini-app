package com.nju.banxing.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: jaggerw
 * @Description: 日期工具类
 * @Date: 2020/11/4
 */
public class DateUtil {
    public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date getCurrentDate(){
        return new Date();
    }

    /**
     * 获取string类型当前时间
     *
     * @return
     */
    public static String getCurrentDateStr() {
        return defaultDateFormat.format(new Date());
    }

    /**
     * 从string类型中解析时间
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date str2Date(String str) throws ParseException {
        return defaultDateFormat.parse(str);
    }

    /**
     * date转成string
     *
     * @param dt
     * @return
     */
    public static String date2Str(Date dt) {
        return defaultDateFormat.format(dt);
    }

    /**
     * 判断时间大小
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int compareDate(Date date1, Date date2) {
        return Long.compare(date1.getTime(), date2.getTime());
    }

    /**
     * 判断时间大小
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compareDate(String DATE1, String DATE2) {
        try {
            Date dt1 = defaultDateFormat.parse(DATE1);
            Date dt2 = defaultDateFormat.parse(DATE2);
            return Long.compare(dt1.getTime(), dt2.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取下一天日期
     *
     * @param date
     * @return
     */
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }

}
