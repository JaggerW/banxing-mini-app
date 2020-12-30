package com.nju.banxing.demo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 评论
 * @Date: 2020/12/30
 */
@Data
public class CommentRequest implements Serializable {

    private static final long serialVersionUID = 2479674921726512288L;

    @NotNull(message = "订单号不能为空")
    private String orderCode;

    @NotNull(message = "请输入评分")
    private Float score;

    @NotNull(message = "请填写评价")
    @Length(max = 140, message = "评价内容请不要超过140字")
    private String content;
}
