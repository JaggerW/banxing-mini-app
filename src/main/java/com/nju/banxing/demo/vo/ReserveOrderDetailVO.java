package com.nju.banxing.demo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: jaggerw
 * @Description: 预约申请订单信息
 * @Date: 2020/12/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReserveOrderDetailVO extends OrderListInfoVO {
    private static final long serialVersionUID = 6266251909338393967L;

    private String consultationContent;
    private String resumeUrl;
    private String rejectReason;
    private String conferenceLink;
    private Integer tutorStatus;
}
