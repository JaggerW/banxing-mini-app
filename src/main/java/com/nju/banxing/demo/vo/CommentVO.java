package com.nju.banxing.demo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 评价
 * @Date: 2020/11/14
 */
@Data
public class CommentVO implements Serializable {
    private static final long serialVersionUID = 2340792211007043188L;

    private String id;
    private String userId;
    private String nickName;
    private String userAvatarUrl;
    private String commentTime;
    private Float commentScore;
    private String commentContent;

}
