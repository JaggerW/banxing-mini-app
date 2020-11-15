package com.nju.banxing.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.nju.banxing.demo.common.TimePair;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Author: jaggerw
 * @Description: 工作时间
 * @Date: 2020/11/14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkTimeVO extends TimePair {
    private static final long serialVersionUID = -5518910567007346410L;

    private Boolean reserveFlag;
    private String dayOfWeek;
    private Long dateTimeStamp;

    @JsonFormat(pattern = "MM月dd日")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;

    private Integer startTimeSecondOfDay;
    private Integer endTimeSecondOfDay;
}
