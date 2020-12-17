package com.nju.banxing.demo.exception;

/**
 * @Author: jaggerw
 * @Description: 重试异常
 * @Date: 2020/12/17
 */
public class RetryException extends GlobalException {

    private static final long serialVersionUID = -7142740646209719624L;

    public RetryException(CodeMsg codeMsg) {
        super(codeMsg);
    }
}
