package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.util.DateUtil;
import com.sun.media.sound.SoftTuning;
import sun.font.Decoration;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * @Author: jaggerw
 * @Description: test
 * @Date: 2020/11/4
 */
public class Demo{
    public static void main(String[] args) {

        LocalDateTime nextMonday = DateUtil.getNextMonday();
        LocalDate localDate = nextMonday.toLocalDate();
        System.out.println(localDate);
        System.out.println(nextMonday);
        System.out.println(DateUtil.toTimeStamp(nextMonday));
        System.out.println(DateUtil.toLocalDate(DateUtil.toTimeStamp(nextMonday)));
        System.out.println(DateUtil.toLocalDateTime(DateUtil.toTimeStamp(nextMonday)));

    }
}
