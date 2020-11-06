package com.nju.banxing.demo.domain;

import lombok.Data;

import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 测试专用
 * @Date: 2020/11/6
 */
@Data
public class TestDO {
    private String id;
    private String name;
    private String token;
    private List<String> list;
}
