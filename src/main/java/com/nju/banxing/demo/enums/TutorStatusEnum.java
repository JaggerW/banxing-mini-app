package com.nju.banxing.demo.enums;

import lombok.Getter;

/**
 * @Author: jaggerw
 * @Description: 订单中导师回复状态
 * @Date: 2020/11/27
 */
@Getter
public enum TutorStatusEnum {

    TO_CONFIRM(0,"待确认"),
    ACCEPTED(1,"已接受"),
    REFUSED(2, "已拒绝"),
    AUTO_REFUSED(3, "已自动拒绝");

    private Integer code;
    private String desc;

    TutorStatusEnum(int c, String d){
        this.code = c;
        this.desc = d;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TutorStatusEnum getEnumByCode(Integer code){
        if(null == code){
            return null;
        }

        for(TutorStatusEnum statusEnum : values()){
            if(code.equals(statusEnum.code)){
                return statusEnum;
            }
        }
        return null;
    }
}
