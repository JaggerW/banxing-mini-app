package com.nju.banxing.demo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 订单状态详情页
 * @Date: 2020/12/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderStatusDetailVO extends OrderListInfoVO {
    private static final long serialVersionUID = 6500890663764661216L;

    private Integer orderStatus;
    private List<String> orderStatusDetail;
    private Boolean processFlag;

}
