package com.nju.banxing.demo.exception;

/**
 * @Author: jaggerw
 * @Description: 异常类
 * @Date: 2020/11/2
 */
public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 6551086887965845508L;

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
