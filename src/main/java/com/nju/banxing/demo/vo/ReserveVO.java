package com.nju.banxing.demo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @Author: jaggerw
 * @Description: 预约界面VO
 * @Date: 2020/11/14
 */
@Data
public class ReserveVO implements Serializable {
    private static final long serialVersionUID = -1413166197296474939L;

    private String nickName;
    private String currentUniversity;
    private String currentProfession;
    private Integer consultationType;

    private Integer dayOfWeek;
    private LocalDateTime date;
    private String dateStr;
    private LocalTime startTime;
    private String startTimeStr;

    private BigDecimal consultationCost;

}
