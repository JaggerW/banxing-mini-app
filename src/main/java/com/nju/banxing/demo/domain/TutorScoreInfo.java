package com.nju.banxing.demo.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 导师成绩相关信息
 * @Date: 2021/1/18
 */
@Data
public class TutorScoreInfo implements Serializable {

    private static final long serialVersionUID = 4592833831577954794L;

    private Float firstScore;
    private Float secondScore;
    private Integer firstRank;
    private Integer firstTotal;
    private Integer secondRank;
    private Integer secondTotal;

    private Float gpa;
    private Integer maxGPA;
    private Integer gpaRank;
    private Integer gpaTotal;
}
