package com.nju.banxing.demo.domain;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 导师表
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-11
 */
@Data
@TableName("banxing_tutor")
public class TutorDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 微信用户openid
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 本科学校
     */
    private String bachelorUniversity;

    /**
     * 本科专业
     */
    private String bachelorProfession;

    /**
     * 当前学校
     */
    private String currentUniversity;

    /**
     * 当前专业
     */
    private String currentProfession;

    /**
     * 指导方向：0001，考研；0010，保研
     */
    private Integer consultationType;

    /**
     * 咨询费用（每十分钟）
     */
    private BigDecimal consultationCost;

    /**
     * 成绩信息
     */
    private String scoreInfo;

    public TutorScoreInfo getScoreInfo(){
        if (StringUtils.isNotEmpty(this.scoreInfo)){
            return JSON.parseObject(this.scoreInfo, TutorScoreInfo.class);
        }
        return null;
    }

    /**
     * 学生证首页
     */
    private String studentCardHome;

    /**
     * 学生证信息页
     */
    private String studentCardInfo;

    /**
     * 学生证注册页
     */
    private String studentCardRegister;

    /**
     * 证明材料（暂留）
     */
    private String evidenceUrl;

    /**
     * 自我介绍
     */
    private String introduction;

    /**
     * 工作时间
     */
    private String workTime;

    /**
     * 咨询人数
     */
    private Integer consultationCount;

    /**
     * 评分
     */
    private Float commentScore;

    /**
     * 审核状态：0，待审核；1，审核通过；2，审核失败
     */
    private Integer status;

    /**
     * 申请时间
     */
    private LocalDateTime applicationTime;

    /**
     * 通过时间
     */
    private LocalDateTime passedTime;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 预留扩展字段
     */
    private String extension;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人id
     */
    private String creator;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 修改人id
     */
    private String modifier;


}
