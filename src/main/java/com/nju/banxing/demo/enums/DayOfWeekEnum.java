package com.nju.banxing.demo.enums;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 一周枚举类
 * @Date: 2020/11/12
 */
public enum DayOfWeekEnum {

    MONDAY(1,"周一"),
    TUESDAY(2,"周二"),
    WEDNESDAY(3,"周三"),
    THURSDAY(4,"周四"),
    FRIDAY(5,"周五"),
    SATURDAY(6,"周六"),
    SUNDAY(7,"周天");


    private Integer code;
    private String desc;

    DayOfWeekEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DayOfWeekEnum getEnumByCode(int code){
        for(DayOfWeekEnum day : values()){
            if(code == day.getCode()){
                return day;
            }
        }
        return null;
    }

}
