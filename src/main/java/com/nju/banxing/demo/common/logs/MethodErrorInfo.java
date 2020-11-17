package com.nju.banxing.demo.common.logs;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 日志异常信息
 * @Date: 2020/11/3
 */
@Data
public class MethodErrorInfo implements Serializable {
    private static final long serialVersionUID = 506848625932261175L;

    private String ip;
    private String url;
    private String httpMethod;
    private String classMethod;
    private Object requestParams;
    private String exceptionMsg;
}
