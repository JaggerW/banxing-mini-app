package com.nju.banxing.demo.common.sms;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 登录验证码模板
 * @Date: 2020/11/3
 */
@Data
public class LoginVerSmsTemplate implements Serializable {
    private static final long serialVersionUID = 4996256496771715079L;

    private String code;
}
