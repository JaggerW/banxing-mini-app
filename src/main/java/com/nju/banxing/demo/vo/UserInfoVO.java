package com.nju.banxing.demo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 用户基本信息
 * @Date: 2020/11/11
 */
@Data
public class UserInfoVO implements Serializable {
    private static final long serialVersionUID = 3445871398987086768L;

    private String nickName;

    private String avatarUrl;

    private String mobile;

    private String email;

    private Boolean adminFlag;

    private Boolean tutorFlag;
}
