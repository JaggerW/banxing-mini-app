package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

/**
 * @Author: jaggerw
 * @Description: test
 * @Date: 2020/11/4
 */
@Slf4j
public class Demo{
    public static void main(String[] args) {

        int value = DateUtil.now().getDayOfWeek().getValue();
        System.out.println(value);
    }

    public static int getValue(){
        try {
            System.out.println(1);
        }catch (Exception e){
            System.out.println(2);
        }finally {
            System.out.println(3);
        }

        return 2;
    }
}
