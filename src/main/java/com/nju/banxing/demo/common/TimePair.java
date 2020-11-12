package com.nju.banxing.demo.common;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * @Author: jaggerw
 * @Description: 时间对
 * @Date: 2020/11/12
 */
@Data
public class TimePair implements Serializable {
    private static final long serialVersionUID = 3313171205318843026L;

    private LocalTime startTime;
    private LocalTime endTime;

}
