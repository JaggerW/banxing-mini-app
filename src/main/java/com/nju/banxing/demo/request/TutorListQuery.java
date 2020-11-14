package com.nju.banxing.demo.request;

import com.nju.banxing.demo.common.BasePaged;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: jaggerw
 * @Description: 主页查询
 * @Date: 2020/11/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TutorListQuery extends BasePaged {
    private static final long serialVersionUID = 6667272747668960198L;

    private Integer consultationType;
    private String keyword;
}
