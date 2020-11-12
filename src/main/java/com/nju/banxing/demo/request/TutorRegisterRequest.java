package com.nju.banxing.demo.request;

import com.nju.banxing.demo.common.TimePair;
import com.sun.security.jgss.InquireSecContextPermission;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 导师注册申请
 * @Date: 2020/11/12
 */
@Data
public class TutorRegisterRequest implements Serializable {
    private static final long serialVersionUID = -1289429884147287091L;

    private String bachelorUniversity;
    private String bachelorProfession;
    private String currentUniversity;
    private String currentProfession;

    // 1,保研；2，考研；（单选）
    private Integer instructionType;

    private BigDecimal consultationCost;
    private Float firstScore;
    private Float secondScore;
    private Integer firstRank;
    private Integer firstTotal;
    private Integer secondRank;
    private Integer secondTotal;
    private String introduction;

    // 证明材料url路径
    private String studentCardHome;
    private String studentCardInfo;
    private String studentCardRegister;

    // 工作时间
    private List<TimePair> workTimeList;


}
