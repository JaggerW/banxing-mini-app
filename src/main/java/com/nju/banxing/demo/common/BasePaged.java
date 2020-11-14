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

    private Long pageIndex = 1L;
    private Long pageSize = 20L;
    private Long offset;
    private Long total;
    private Long pages;

}
