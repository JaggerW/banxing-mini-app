package com.nju.banxing.demo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: jaggerw
 * @Description: 咨询安排
 * @Date: 2020/12/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScheduleListInfoVO extends OrderListInfoVO {
    private static final long serialVersionUID = 7294407672658981213L;

    private String conferenceLink;
    private String conferenceUrl;
}
