package com.nju.banxing.demo.request;

import com.nju.banxing.demo.common.TimePair;
import com.sun.security.jgss.InquireSecContextPermission;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "本科学校不能为空")
    private String bachelorUniversity;

    @NotNull(message = "本科专业不能为空")
    private String bachelorProfession;

    @NotNull(message = "当前学校不能为空")
    private String currentUniversity;

    @NotNull(message = "当前专业不能为空")
    private String currentProfession;

    // 1,保研；2，考研；（单选）
    @NotNull(message = "请选择指导方向")
    private Integer consultationType;

    @NotNull(message = "请填写咨询费用")
    private BigDecimal consultationCost;

    @NotNull(message = "初试分数不能为空")
    private Float firstScore;

    @NotNull(message = "复试分数不能为空")
    private Float secondScore;

    @NotNull(message = "初试排名不能为空")
    private Integer firstRank;

    @NotNull(message = "初试排名不能为空")
    private Integer firstTotal;

    @NotNull(message = "复试排名不能为空")
    private Integer secondRank;

    @NotNull(message = "复试排名不能为空")
    private Integer secondTotal;

    @Length(min = 100, message = "自我介绍至少需要100字以上")
    private String introduction;

    // 证明材料url路径
    private String studentCardHome;
    private String studentCardInfo;
    private String studentCardRegister;

    // 工作时间
    private List<TimePair> workTimeList;


}
