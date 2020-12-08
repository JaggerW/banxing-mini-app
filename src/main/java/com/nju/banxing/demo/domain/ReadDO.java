package com.nju.banxing.demo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: jaggerw
 * @Description: 已读时间表
 * @Date: 2020/12/8
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("banxing_read")
public class ReadDO implements Serializable {
    private static final long serialVersionUID = 3650749246929018522L;

    /**
     * user openid
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 预约申请上次已读时间
     */
    private LocalDateTime orderApply;

    /**
     * 预约反馈上次已读时间
     */
    private LocalDateTime orderReply;

    /**
     * 订单评价上次已读时间
     */
    private LocalDateTime orderComment;

    /**
     * 预留扩展字段
     */
    private String extension;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
