package com.nju.banxing.demo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @Author: jaggerw
 * @Description: 创建订单请求类
 * @Date: 2020/11/19
 */
@Data
public class OrderCreateRequest implements Serializable {
    private static final long serialVersionUID = -1261690215264235171L;

    @NotNull(message = "dupKey不能为空")
    private String dupKey;

    @NotNull(message = "tutorId不能为空")
    private String tutorId;

    @NotNull(message = "咨询类型不能为空")
    private Integer consultationType;

    @NotNull(message = "咨询日期不能为空")
    private Long reserveDateTimeStamp;

    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime reserveStartTime;

    /**
     * 咨询时长(totalTime = consultationTimeCount * 10)
     */
    @NotNull(message = "咨询时长不能为空")
    private Integer consultationTimeCount;

    @NotNull(message = "问题描述不能为空")
    private String consultationContent;

    private String resumeUrl;


}
