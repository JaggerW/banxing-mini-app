package com.nju.banxing.demo.domain;

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
 * 导师评价表
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("comment")
public class CommentDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 被评价用户openid（导师）
     */
    private String tutorId;

    /**
     * 评价者openid（普通用户）
     */
    private String userId;

    /**
     * 预约单单号
     */
    private String orderCode;

    /**
     * 评价者用户头像
     */
    private String userAvatarUrl;

    /**
     * 咨询类型
     */
    private Integer consultationType;

    /**
     * 评价时间
     */
    private LocalDateTime commentTime;

    /**
     * 评价分数
     */
    private Float commentScore;

    /**
     * 评价内容
     */
    private String commentContent;

    /**
     * 扩展
     */
    private String extension;

    /**
     * 创建日期
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改日期
     */
    private LocalDateTime modifyTime;

    /**
     * 修改人
     */
    private String modifier;


}
