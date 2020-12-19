package com.nju.banxing.demo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 退款返回
 * @Date: 2020/12/19
 */
@Data
public class WxRefundVO implements Serializable {
    private static final long serialVersionUID = 6921600502573039466L;

    private String transactionId;
    private String orderCode;
    private String orderRefundCode;
    private String refundId;
    private Integer refundFee;
}
