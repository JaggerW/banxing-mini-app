package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.WxMessageUtil;
import com.nju.banxing.demo.vo.WxMessageVO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @Author: jaggerw
 * @Description: test
 * @Date: 2020/11/4
 */
@Slf4j
public class Demo{
    public static void main(String[] args) {
        String timeStr = "2020/1/1 21:16-22:14";
        LocalDateTime meetingStartTime = WxMessageUtil.getMeetingStartTime(timeStr);
        System.out.println(meetingStartTime);
    }
}
