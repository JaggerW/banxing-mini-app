package com.nju.banxing.demo.enums;

/**
 * @Author: jaggerw
 * @Description: 订单操作类型
 * @Date: 2020/11/30
 */
public enum OrderProcessTypeEnum {
    SUCCESS(0,"操作成功"),
    FAIL(1,"操作失败"),
    ERROR(2,"操作异常");

    private Integer code;
    private String desc;

    OrderProcessTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
