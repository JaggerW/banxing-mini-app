package com.nju.banxing.demo.vo;

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

    private LocalDateTime date;
    private String dateStr;
}
