package com.nju.banxing.demo.request;

import lombok.Data;

import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 测试专用
 * @Date: 2020/11/6
 */
@Data
public class TestRequest {
    private String id;
    private String name;
    private List<String> list;
}
