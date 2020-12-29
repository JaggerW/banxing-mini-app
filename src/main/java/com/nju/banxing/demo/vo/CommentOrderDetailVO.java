package com.nju.banxing.demo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: jaggerw
 * @Description: 评价订单详情
 * @Date: 2020/12/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentOrderDetailVO extends OrderListInfoVO {
    private static final long serialVersionUID = 1131513163701503137L;

    private String currentUniversity;
    private String currentProfession;

    private Float commentScore;
    private String commentContent;
}
