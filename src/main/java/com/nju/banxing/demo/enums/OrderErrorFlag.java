package com.nju.banxing.demo.enums;

/**
 * @Author: jaggerw
 * @Description: 订单异常标志位
 * @Date: 2020/12/21
 */
public enum OrderErrorFlag {

    NORMAL(0,"正常"),
    UN_NORMAL(1,"异常");


    private Integer code;
    private String desc;

    OrderErrorFlag(int c, String d){
        this.code = c;
        this.desc = d;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OrderErrorFlag getEnumByCode(Integer code){
        if(null == code){
            return null;
        }

        for(OrderErrorFlag statusEnum : values()){
            if(code.equals(statusEnum.code)){
                return statusEnum;
            }
        }
        return null;
    }
}
