package com.nju.banxing.demo.util;

import com.google.common.collect.Lists;
import com.nju.banxing.demo.vo.WxMessageVO;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 小程序通知工具类
 * @Date: 2020/11/9
 */
public class WxMessageUtil {

    private static final String MEETING_TIME_KEY_POINT = "会议时间：";
    private static final String MEETING_URL_KEY_POINT = "会议列表：";
    private static final String MEETING_ID_KEY_POINT = "会议 ID：";
    private static final String MEETING_SECRET_POINT = "会议密码：";

    /**
     * 从腾讯视频会议链接中解析time，url，id并按此顺序返回
     * @param meetingStr
     * @return
     */
    public static WxMessageVO parseMes(String meetingStr) {
        if(StringUtils.isEmpty(meetingStr)){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] split = meetingStr.split("\n");
        for (String s : split) {
            sb.append(s);
        }
        meetingStr = String.valueOf(sb);
        WxMessageVO vo = new WxMessageVO();
        vo.setMeetingTime(getMeetingTime(meetingStr));
        vo.setMeetingId(getMeetingID(meetingStr));
        vo.setMeetingSecret(getMeetingSecret(meetingStr));
        vo.setMeetingUrl(getMeetingURL(meetingStr));
        return vo;
    }

    private static String getMeetingTime(String str) {
        int index = str.indexOf(MEETING_TIME_KEY_POINT);
        if(index == -1){
            return "";
        }
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
        if(index == -1){
            return "";
        }
        index += MEETING_URL_KEY_POINT.length();
        return str.substring(index, index + 42);
    }

    private static String getMeetingID(String str) {
        int index = str.indexOf(MEETING_ID_KEY_POINT);
        if(index == -1){
            return "";
        }
        index += MEETING_ID_KEY_POINT.length();
        return str.substring(index, index + 11);
    }

    private static String getMeetingSecret(String str){
        int index = str.indexOf(MEETING_SECRET_POINT);
        if(index == -1){
            return "无";
        }
        index += MEETING_SECRET_POINT.length();
        StringBuffer sb = new StringBuffer();
        char c = str.charAt(index);
        while (Character.isDigit(c)) {
            sb.append(c);
            c = str.charAt(++index);
        }
        return String.valueOf(sb);
    }
}
