package com.nju.banxing.demo.enums;

/**
 * @Author: jaggerw
 * @Description: 资金
 * @Date: 2020/12/1
 */
public enum CoinProcessTypeEnum {
    PAY(1,"付款"),
    ENABLE(2,"可使用"),
    REFUND(3,"退款"),
    DRAW(4,"提款");

    private Integer code;
    private String desc;

    CoinProcessTypeEnum(Integer code, String desc){
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
