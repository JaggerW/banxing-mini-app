package com.nju.banxing.demo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @Author: jaggerw
 * @Description: 微信小程序通知
 * @Date: 2020/12/17
 */
@Data
public class WxMessageVO implements Serializable {
    private static final long serialVersionUID = -536210020942988051L;

    private String meetingId;
    private String meetingSecret;
    private String meetingUrl;
    private String meetingTime;
    private LocalDateTime meetingStartTime;
}
