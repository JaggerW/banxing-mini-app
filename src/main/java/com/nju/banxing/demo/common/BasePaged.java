package com.nju.banxing.demo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 分页的基类
 * @Date: 2020/11/2
 */
@Data
public class BasePaged implements Serializable {
    private static final long serialVersionUID = 6008855424801110010L;

    private Integer index = 0;
    private Integer rowNum = 20;
    private Long offset;
    private Long total;

}
