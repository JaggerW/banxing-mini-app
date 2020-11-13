package com.nju.banxing.demo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: jaggerw
 * @Description: 导师基本信息
 * @Date: 2020/11/13
 */
@Data
public class TutorSimpleInfoVO implements Serializable {
    private static final long serialVersionUID = -3438130164315198280L;

    private String openid;
    private String nickName;
    private String currentUniversity;
    private String currentProfession;
    private Float commentScore;
    private Integer consultationCount;
    private BigDecimal consultationCost;
    private String bachelorUniversity;
    private String bachelorProfession;
    private String introduction;
    private Float firstScore;
    private Integer firstRank;
    private Integer firstTotal;
    private Float secondScore;
    private Integer secondRank;
    private Integer secondTotal;

}
