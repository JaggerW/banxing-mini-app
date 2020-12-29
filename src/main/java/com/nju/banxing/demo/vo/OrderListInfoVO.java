package com.nju.banxing.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @Author: jaggerw
 * @Description: 预约申请处理
 * @Date: 2020/12/11
 */
@Data
public class OrderListInfoVO implements Serializable {
    private static final long serialVersionUID = -6287907901155756527L;

    private String nickName;
    private String orderCode;

    @JsonFormat(pattern = "MM月dd日")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate reserveDate;

    @JsonFormat(pattern = "HH:mm")
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime reserveStartTime;

    @JsonFormat(pattern = "HH:mm")
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime reserveEndTime;
}
