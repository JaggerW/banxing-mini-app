package com.nju.banxing.demo.common.logs;


import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 日志信息类
 * @Date: 2020/11/3
 */
@Data
public class MethodLogInfo implements Serializable {
    private static final long serialVersionUID = -8264875606884374895L;

    private String ip;
    private String url;
    private String httpMethod;
    private String classMethod;
    private Object requestParams;
    private Object result;
    private Long timeCost;

}
