package com.nju.banxing.demo.domain;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order")
public class OrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单唯一编号
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户openid
     */
    private String userId;

    /**
     * 导师openid
     */
    private String tutorId;

    /**
     * 咨询类型：1，保研；2，考研
     */
    private Integer consultationType;

    /**
     * 预约时间
     */
    private Date reserveDate;

    /**
     * 咨询开始时间
     */
    private Date reserveStartTime;

    /**
     * 咨询结束时间
     */
    private Date reserveEndTime;

    /**
     * 咨询费用单价（/min）
     */
    private BigDecimal consultationCost;

    /**
     * 咨询费用总价
     */
    private BigDecimal totalCost;

    /**
     * 咨询时长
     */
    private Integer consultationTime;

    /**
     * 会议原始链接
     */
    private String conferenceLink;

    /**
     * 会议号
     */
    private String conferenceCode;

    /**
     * 会议密码
     */
    private String conferenceSecret;

    /**
     * 订单状态：默认10，初始化，暂不使用；20开始为正常态
     */
    private Integer orderStatus;

    /**
     * 导师确认状态：0，未处理；1，已同意；2，已拒绝
     */
    private Integer tutorStatus;

    /**
     * 拒绝理由
     */
    private String rejectReason;

    /**
     * 评论状态：0，未评论；1，已评论；2，已过期自动评论
     */
    private Integer commentStatus;

    /**
     * 版本号
     */
    @Version
    private Integer version;

    /**
     * 异常标志位：0，正常；1，异常（退款失败）
     */
    private Boolean errorFlag;

    /**
     * 预留扩展字段
     */
    private String extension;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人openid
     */
    private String creator;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 修改人openid
     */
    private String modifier;


}
