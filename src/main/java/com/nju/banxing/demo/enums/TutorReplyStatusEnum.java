package com.nju.banxing.demo.enums;

/**
 * @Author: jaggerw
 * @Description: 导师状态申请
 * @Date: 2020/11/13
 */
public enum TutorReplyStatusEnum {

    NULL(-1,"无状态"),
    TO_VERIFY(0,"待审核"),
    VERIFY_PASS(1,"审核通过"),
    VERIFY_REJECT(2,"审核失败");

    private Integer code;
    private String desc;

    TutorReplyStatusEnum(Integer c, String d){
        this.code = c;
        this.desc = d;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
