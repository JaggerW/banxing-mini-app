package com.nju.banxing.demo.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单流水表
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("banxing_order_log")
public class OrderLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 操作之前订单状态，默认为10，预留初始化状态
     */
    private Integer preStatus;

    /**
     * 操作之后订单状态
     */
    private Integer afterStatus;

    /**
     * 操作类型
     */
    private Integer processType;

    /**
     * 操作内容
     */
    private String processContent;

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
