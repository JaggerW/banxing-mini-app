package com.nju.banxing.demo.enums;


/**
 * @Author: jaggerw
 * @Description: 用户角色
 * @Date: 2021/1/18
 */

public enum UserRoleEnum {

    ROOT("ROOT","无状态");

    private String code;
    private String desc;

    UserRoleEnum(String c, String d){
        this.code = c;
        this.desc = d;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserRoleEnum getEnumByCode(String code){

        for(UserRoleEnum roleEnum : values()){
            if(roleEnum.code.equals(code)){
                return roleEnum;
            }
        }
        return null;
    }
}
