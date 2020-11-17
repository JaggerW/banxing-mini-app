package com.nju.banxing.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

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

    @JsonFormat(pattern = "yyyy.MM.dd")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime commentTime;

    private Long commentTimeStamp;
    private Float commentScore;
    private String commentContent;

}
