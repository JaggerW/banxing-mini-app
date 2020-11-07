package com.nju.banxing.demo.common.wx;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 微信小程序用户信息类
 * @Date: 2020/11/7
 */
@Data
public class WxUserInfo implements Serializable {
    private static final long serialVersionUID = -4104564256059209932L;

    private String nickName;
    private String avatarUrl;
    // 0 未知，1 男性，2 女性
    private Integer gender;
    private String country;
    private String province;
    private String city;


}
