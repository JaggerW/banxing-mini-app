package com.nju.banxing.demo.request;

import com.nju.banxing.demo.common.TimePair;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 导师基础信息基类
 * @Date: 2020/12/11
 */
@Data
public class BaseTutorInfo implements Serializable {
    private static final long serialVersionUID = 8075103501347669412L;

    @NotNull(message = "本科学校不能为空")
    private String bachelorUniversity;

    @NotNull(message = "本科专业不能为空")
    private String bachelorProfession;

    // 1,保研；2，考研；（单选）
    @NotNull(message = "请选择指导方向")
    private Integer consultationType;

    @NotNull(message = "请填写咨询费用")
    private BigDecimal consultationCost;

    // 若保研则可以为空
    private Float firstScore;

    private Float secondScore;

    private Integer firstRank;

    private Integer firstTotal;

    private Integer secondRank;

    private Integer secondTotal;

    // 若考研则可以为空
    private Float gpa;

    private Integer maxGPA;

    private Integer gpaRank;

    private Integer gpaTotal;

    @Length(min = 100, message = "自我介绍至少需要100字以上")
    @Length(max = 500, message = "自我介绍不得超过500字以上")
    private String introduction;

    // 工作时间
    @NotNull(message = "请设置工作时间")
    private List<TimePair> workTimeList;
}
