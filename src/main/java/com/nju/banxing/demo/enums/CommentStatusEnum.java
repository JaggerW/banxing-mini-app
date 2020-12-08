package com.nju.banxing.demo.enums;

/**
 * @Author: jaggerw
 * @Description: 评论状态
 * @Date: 2020/11/27
 */
public enum CommentStatusEnum {

    NULL(-1,"无状态"),
    TO_COMMENT(0,"待评价"),
    COMMENTED(1, "已评价"),
    AUTO_COMMENTED(2,"已自动评价");

    private Integer code;
    private String desc;

    CommentStatusEnum(int c, String d){
        this.code = c;
        this.desc = d;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static CommentStatusEnum getEnumByCode(Integer code){
        if(null == code){
            return null;
        }

        for(CommentStatusEnum statusEnum : values()){
            if(code.equals(statusEnum.code)){
                return statusEnum;
            }
        }
        return null;
    }
}
