package com.nju.banxing.demo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: jaggerw
 * @Description: 反馈订单详情
 * @Date: 2020/12/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReplyOrderDetailVO extends OrderListInfoVO {
    private static final long serialVersionUID = -7036640876391849513L;

    private String currentUniversity;
    private String currentProfession;
    private String conferenceLink;
    private String rejectReason;
}
