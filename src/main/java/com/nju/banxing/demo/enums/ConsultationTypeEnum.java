package com.nju.banxing.demo.enums;


import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 咨询类型
 * @Date: 2020/11/11
 */
public enum ConsultationTypeEnum {

    KAO_YAN(1,"考研"),
    BAO_YAN(2,"保研");

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

    public static ConsultationTypeEnum getEnumByCode(Integer code){
        if(null == code){
            return null;
        }
        for(ConsultationTypeEnum typeEnum : values()){
            if(typeEnum.getCode().equals(code)){
                return typeEnum;
            }
        }
        return null;
    }
}
