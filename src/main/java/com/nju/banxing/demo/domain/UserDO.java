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
 * 用户表
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-11
 */
@Data
@TableName("banxing_user")
public class UserDO implements Serializable {

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
     * 电话
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 是否为导师：0，不是；1，是
     */
    private Boolean tutorFlag;

    /**
     * 是否为管理员：0，不是；1，是
     */
    private Boolean adminFlag;

    /**
     * 短信发送许可：1，同意；0，拒绝
     */
    private Boolean smsPermission;

    /**
     * 邮箱发送许可：1，同意；0，拒绝
     */
    private Boolean emailPermission;

    /**
     * 性别：0，未知；1，男；2，女
     */
    private Integer gender;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 上次登录时间
     */
    private LocalDateTime latestLoginTime;

    /**
     * 登录次数
     */
    private Long loginCount;

    /**
     * 扩展字段，留备用
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
