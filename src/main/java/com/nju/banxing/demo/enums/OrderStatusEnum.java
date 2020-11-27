package com.nju.banxing.demo.enums;

import lombok.Getter;

/**
 * @Author: jaggerw
 * @Description: 订单状态
 * @Date: 2020/11/27
 */
public enum OrderStatusEnum {

    // 预留状态，暂不使用
    ORDER_INIT(10,"订单未支付"),

    // 订单正常进行中状态
    ORDER_TO_PAY(20,"订单付款中"),
    ORDER_PAID(30,"订单已付款"),
    TUTOR_ACCEPT(40,"导师已确认"),
    ORDER_COMPLETE(50,"订单已完成"),

    // 订单已结束状态
    ORDER_CLOSED(100,"订单已关闭"),
    ORDER_FAIL_PAY(130,"订单支付失败"),
    TUTOR_REFUSE(140,"导师已拒绝"),
    ORDER_REFUNDED(150,"订单已退款");

    private Integer code;
    private String desc;

    OrderStatusEnum(int c, String d){
        this.code = c;
        this.desc = d;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public OrderStatusEnum getEnumByCode(Integer code){
        if(null == code){
            return null;
        }

        for(OrderStatusEnum statusEnum : values()){
            if(code.equals(statusEnum.code)){
                return statusEnum;
            }
        }
        return null;
    }
}
