package com.nju.banxing.demo.enums;

/**
 * @Author: jaggerw
 * @Description: 记录可见状态
 * @Date: 2020/12/24
 */
public enum RowStatusEnum {

    VALID(0,"有效的"),
    INVALID(1,"无效的");


    private Integer code;
    private String desc;

    RowStatusEnum(int c, String d){
        this.code = c;
        this.desc = d;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RowStatusEnum getEnumByCode(Integer code){
        if(null == code){
            return null;
        }

        for(RowStatusEnum statusEnum : values()){
            if(code.equals(statusEnum.code)){
                return statusEnum;
            }
        }
        return null;
    }
}
