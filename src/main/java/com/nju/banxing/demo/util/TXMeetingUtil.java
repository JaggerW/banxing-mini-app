package com.nju.banxing.demo.util;

import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.vo.TXMeetingInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * @Author: jaggerw
 * @Description: 小程序通知工具类
 * @Date: 2020/11/9
 */
@Slf4j
public class TXMeetingUtil {

    private static final String MEETING_TIME_KEY_POINT = "会议时间：";
    private static final String MEETING_URL_KEY_POINT = "会议列表：";
    private static final String MEETING_ID_KEY_POINT = "会议 ID：";
    private static final String MEETING_SECRET_POINT = "会议密码：";

    /**
     * 从腾讯视频会议链接中解析time，url，id并按此顺序返回
     * @param meetingStr
     * @return
     */
    public static TXMeetingInfoVO parseMes(String meetingStr) {
        try {
            if(StringUtils.isEmpty(meetingStr)){
                return null;
            }
            StringBuilder sb = new StringBuilder();
            String[] split = meetingStr.split("\n");
            for (String s : split) {
                sb.append(s);
            }
            meetingStr = String.valueOf(sb);
            TXMeetingInfoVO vo = new TXMeetingInfoVO();
            vo.setMeetingTime(getMeetingTime(meetingStr));
            vo.setMeetingId(getMeetingID(meetingStr));
            vo.setMeetingSecret(getMeetingSecret(meetingStr));
            vo.setMeetingUrl(getMeetingURL(meetingStr));
            vo.setMeetingStartTime(getMeetingStartTime(vo.getMeetingTime()));
            return vo;
        }catch (Exception e){
            throw new GlobalException(CodeMsg.ERROR_MEETING_MESSAGE);
        }

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

    private static LocalDateTime getMeetingStartTime(String timeStr){

        try {

            String[] dateAndTime = timeStr.split(" ");
            String date = dateAndTime[0];
            String time = dateAndTime[1];
            String[] dates = date.split("/");
            String year = dates[0];
            String month = dates[1];
            if(month.length() == 1){
                month = "0" + month;
            }
            String day = dates[2];
            if(day.length() == 1){
                day = "0" + day;
            }
            String[] times = time.split("-");
            String startTime = times[0];
            startTime = startTime + ":00";
            String parseTime = year + "-" + month + "-" + day + " " + startTime;
            return LocalDateTime.parse(parseTime, DateUtil.dtf);

        } catch (Exception e){
            log.error("时间解析错误，被解析时间字符串为：" + timeStr);
            throw new GlobalException(CodeMsg.ERROR_PARSE_TIME);
        }


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
