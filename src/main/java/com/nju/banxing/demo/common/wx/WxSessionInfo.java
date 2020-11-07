package com.nju.banxing.demo.common.wx;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 微信小程序session信息类
 * @Date: 2020/11/5
 */
@Data
public class WxSessionInfo implements Serializable {
    private static final long serialVersionUID = 3941607680534582583L;

    private String openId;
    private String sessionKey;

    public WxSessionInfo(){}

    public WxSessionInfo(String o, String s){
        this.openId = o;
        this.sessionKey = s;
    }
}
