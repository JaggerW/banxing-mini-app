package com.nju.banxing.demo.domain;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 资金流水表
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("banxing_coin_log")
public class CoinLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 系统内部预约咨询订单号
     */
    private String orderCode;

    /**
     * 原账号openid
     */
    private String sourceId;

    /**
     * 目标账号openid
     */
    private String targetId;

    /**
     * 商户号
     */
    private String merchantCode;

    /**
     * 移动金额
     */
    private BigDecimal coinAmount;

    /**
     * 操作类型：1，付款；2，退款；3，提现
     */
    private Integer processType;

    /**
     * 预留扩展字段
     */
    private String extension;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人openid
     */
    private String creator;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 修改人openid
     */
    private String modifier;


}
