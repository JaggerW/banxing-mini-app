package com.nju.banxing.demo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Author: jaggerw
 * @Description: 预约界面VO
 * @Date: 2020/11/14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReserveVO extends WorkTimeVO {
    private static final long serialVersionUID = -1413166197296474939L;

    private String nickName;
    private String currentUniversity;
    private String currentProfession;
    private Integer consultationType;
    private BigDecimal consultationCost;

}
