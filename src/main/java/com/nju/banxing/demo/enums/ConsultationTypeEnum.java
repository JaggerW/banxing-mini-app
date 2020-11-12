package com.nju.banxing.demo.enums;


import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 咨询类型
 * @Date: 2020/11/11
 */
public enum ConsultationTypeEnum {

    BAO_YAN(1,"保研"),
    KAO_YAN(2,"考研");

    private Integer code;
    private String desc;

    ConsultationTypeEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return desc;
    }

//    public static ConsultationTypeEnum getEnumByCode(int code){
//        for(ConsultationTypeEnum typeEnum : values()){
//            if(typeEnum.getCode() == code){
//                return typeEnum;
//            }
//        }
//        return null;
//    }
}
