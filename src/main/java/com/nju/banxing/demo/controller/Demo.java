package com.nju.banxing.demo.controller;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: jaggerw
 * @Description: test
 * @Date: 2020/11/4
 */
@Slf4j
public class Demo{
    public static void main(String[] args) {

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
