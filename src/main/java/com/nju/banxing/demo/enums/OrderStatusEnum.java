package com.nju.banxing.demo.enums;


/**
 * @Author: jaggerw
 * @Description: 订单状态
 * @Date: 2020/11/27
 */
public enum OrderStatusEnum {

    // 预留状态，暂不使用
    ORDER_INIT(10,"订单未支付"){
        @Override
        public OrderStatusEnum getNext(boolean successFlag) {
            return ORDER_TO_PAY;
        }
    },

    // 订单正常进行中状态
    ORDER_TO_PAY(20,"订单付款中"){
        @Override
        public OrderStatusEnum getNext(boolean successFlag) {
            if(successFlag){
                return ORDER_PAID;
            }else {
                return ORDER_FAIL_PAY;
            }
        }
    },
    ORDER_PAID(30,"订单已付款"){
        @Override
        public OrderStatusEnum getNext(boolean successFlag) {
            if(successFlag){
                return ORDER_PROCESSING;
            }else {
                return APPLY_REFUND;
            }
        }
    },
    ORDER_PROCESSING(40,"订单进行中"){
        @Override
        public OrderStatusEnum getNext(boolean successFlag) {
            return ORDER_COMPLETE;
        }
    },
    ORDER_COMPLETE(50,"订单已完成"){
        @Override
        public OrderStatusEnum getNext(boolean successFlag) {
            return ORDER_CLOSED;
        }
    },

    // 订单已结束状态
    ORDER_CLOSED(100,"订单已关闭"){
        @Override
        public OrderStatusEnum getNext(boolean successFlag) {
            return null;
        }
    },
    ORDER_FAIL_PAY(130,"订单支付失败"){
        @Override
        public OrderStatusEnum getNext(boolean successFlag) {
            return null;
        }
    },
    APPLY_REFUND(90,"申请退款中"){
        @Override
        public OrderStatusEnum getNext(boolean successFlag) {
            return ORDER_REFUNDED;
        }
    },
    ORDER_REFUNDED(150,"订单已退款"){
        @Override
        public OrderStatusEnum getNext(boolean successFlag) {
            return null;
        }
    };


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

    public static OrderStatusEnum getEnumByCode(Integer code){
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

    abstract public OrderStatusEnum getNext(boolean successFlag);
}
