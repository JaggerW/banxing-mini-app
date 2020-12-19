package com.nju.banxing.demo.request;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 退款请求
 * @Date: 2020/12/19
 */
@Data
public class WxRefundRequest implements Serializable {
    private static final long serialVersionUID = 8156745179973812076L;

    private String nonceStr;
    private String orderCode;
    private String orderRefundCode;
    private Integer totalFee;
    private Integer refundFee;
    private String refundDesc;
    private String notifyUrl;

}
