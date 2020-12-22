package com.nju.banxing.demo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 未读
 * @Date: 2020/12/22
 */
@Data
public class UnreadVO implements Serializable {
    private static final long serialVersionUID = -608120838110724753L;

    private Long orderApply;

    private Long orderReply;

    private Long orderComment;
}
