package com.nju.banxing.demo.controller;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: jaggerw
 * @Description: TODO
 * @Date: 2020/11/4
 */
public class Demo{
    public static void main(String[] args) {
        String meeting = "JaggerW 邀请您参加腾讯会议\n" +
                "会议主题：JaggerW预定的会议\n" +
                "会议时间：2020/11/8 22:00-22:30\n" +
                "\n" +
                "点击链接入会，或添加至会议列表：\n" +
                "https://meeting.tencent.com/s/hHNlX6Kwvnci\n" +
                "\n" +
                "会议 ID：382 197 699\n" +
                "\n" +
                "手机一键拨号入会\n" +
                "+8675536550000,,382197699# (中国大陆)\n" +
                "+85230018898,,,2,382197699# (中国香港)\n" +
                "\n" +
                "根据您的位置拨号\n" +
                "+8675536550000 (中国大陆)\n" +
                "+85230018898 (中国香港)\n" +
                "\n";
        getMeetingTime(meeting,"会议时间：");
        getMeetingURL(meeting,"会议列表：");
        getMeetingID(meeting,"会议 ID：");

    }

    public static void getMeetingTime(String str, String keyPoint){
        int index = str.indexOf(keyPoint);
        index +=keyPoint.length();
        StringBuffer sb = new StringBuffer();
        char c = str.charAt(index);
        while (c=='/'||c==' '||c=='-'||c==':'||Character.isDigit(c)){
            sb.append(c);
            c=str.charAt(++index);
            if(index>=str.length()){
                break;
            }
        }
        System.out.println(sb);
    }

    public static void getMeetingURL(String str, String keyPoint){
        int index = str.indexOf(keyPoint);
        index+=keyPoint.length();
        String res = str.substring(index,index+42);
        System.out.println(res);
    }


    public static void getMeetingID(String str, String keyPoint){
        int index = str.indexOf(keyPoint);
        index+=keyPoint.length();
        String res = str.substring(index,index+11);
        System.out.println(res);
    }
}
