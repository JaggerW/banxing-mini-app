package com.nju.banxing.demo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 导师详情页
 * @Date: 2020/11/14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TutorDetailInfoVO extends TutorSimpleInfoVO {
    private static final long serialVersionUID = -5312148173157029745L;

    private BigDecimal consultationCost;
    private List<WorkTimeVO> workTimeList;
}
