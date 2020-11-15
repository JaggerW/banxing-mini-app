package com.nju.banxing.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
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

    private Integer key;
    private String dayOfWeek;

    @JsonFormat(pattern = "MM月dd日")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;

    private Long dateTimeStamp;

    @JsonFormat(pattern = "HH:mm")
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime startTime;

    private Integer startTimeSecondOfDay;

    private BigDecimal consultationCost;

}
