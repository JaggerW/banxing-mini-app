package com.nju.banxing.demo.util;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 小程序通知工具类
 * @Date: 2020/11/9
 */
public class MessageUtil {

    private static final String MEETING_TIME_KEY_POINT = "会议时间：";
    private static final String MEETING_URL_KEY_POINT = "会议列表：";
    private static final String MEETING_ID_KEY_POINT = "会议 ID：";

    /**
     * 从腾讯视频会议链接中解析time，url，id并按此顺序返回
     * @param meetingStr
     * @return
     */
    public static List<String> parseMes(String meetingStr) {
        StringBuilder sb = new StringBuilder();
        String[] split = meetingStr.split("\n");
        for (String s : split) {
            sb.append(s);
        }
        meetingStr = String.valueOf(sb);
        List<String> res = Lists.newArrayList();
        res.add(getMeetingTime(meetingStr));
        res.add(getMeetingURL(meetingStr));
        res.add(getMeetingID(meetingStr));
        return res;
    }

    private static String getMeetingTime(String str) {
        int index = str.indexOf(MEETING_TIME_KEY_POINT);
        index += MEETING_TIME_KEY_POINT.length();
        StringBuffer sb = new StringBuffer();
        char c = str.charAt(index);
        while (c == '/' || c == ' ' || c == '-' || c == ':' || Character.isDigit(c)) {
            sb.append(c);
            c = str.charAt(++index);
        }
        return String.valueOf(sb);
    }

    private static String getMeetingURL(String str) {
        int index = str.indexOf(MEETING_URL_KEY_POINT);
        index += MEETING_URL_KEY_POINT.length();
        return str.substring(index, index + 42);
    }

    private static String getMeetingID(String str) {
        int index = str.indexOf(MEETING_ID_KEY_POINT);
        index += MEETING_ID_KEY_POINT.length();
        return str.substring(index, index + 11);
    }
}
