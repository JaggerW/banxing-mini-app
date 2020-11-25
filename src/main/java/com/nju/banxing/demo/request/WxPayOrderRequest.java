package com.nju.banxing.demo.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 微信支付统一下单
 * @Date: 2020/11/25
 */
@Data
public class WxPayOrderRequest implements Serializable {
    private static final long serialVersionUID = -3001294010172888899L;

    private String body;
    private String detail;
    private String nonceStr;
    private String outTradeNo;
    private Integer totalTee;
    private String openid;
    private String ip;
    private String notifyUrl;
    private String tradeType = "JSAPI";
}
