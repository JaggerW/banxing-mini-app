package com.nju.banxing.demo.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 微信支付预订单
 * @Date: 2020/12/3
 */
@Data
public class WxPayOrderVO implements Serializable {

    private static final long serialVersionUID = 471295807208999526L;

    private String timeStamp;
    private String nonceStr;
    @JSONField(name = "package")
    private String packageValue;
    private String paySign;
    private String orderCode;


}
