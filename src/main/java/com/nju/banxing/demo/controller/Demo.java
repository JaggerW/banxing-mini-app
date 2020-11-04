package com.nju.banxing.demo.controller;

/**
 * @Author: jaggerw
 * @Description: TODO
 * @Date: 2020/11/4
 */
public class Demo extends Thread{
    public static void main(String[] args) {
        try {
            args = null;
            args[0] = "test";
            System.out.println(args[0]);
        }catch (Exception ex){
            System.out.println("Exception");
        }
    }
}
