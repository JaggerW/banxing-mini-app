package com.nju.banxing.demo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: TODO
 * @Date: 2020/11/3
 */
@Data
public class LoginVerSmsTemplate implements Serializable {
    private static final long serialVersionUID = 4996256496771715079L;

    private String code;
}
