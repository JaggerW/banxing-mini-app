package com.nju.banxing.demo.request;

import com.nju.banxing.demo.common.BasePaged;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: jaggerw
 * @Description: 评论请求
 * @Date: 2020/11/14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentListQuery extends BasePaged {
    private static final long serialVersionUID = 1883662235303170505L;

    private String tutorId;
    private Integer consultationType;
}
