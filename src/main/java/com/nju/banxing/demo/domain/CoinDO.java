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
 * 用户资金表
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coin")
public class CoinDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户openid
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 当前可用余额
     */
    private BigDecimal availableAmount;

    /**
     * 当前已占用余额，尚未入账
     */
    private BigDecimal occupyAmount;

    /**
     * 版本号
     */
    @Version
    private Integer version;

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
