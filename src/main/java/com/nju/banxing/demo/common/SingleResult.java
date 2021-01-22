package com.nju.banxing.demo.common;

import com.nju.banxing.demo.exception.CodeMsg;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 返回类型
 * @Date: 2020/11/3
 */
@Data
public class SingleResult<T> implements Serializable {
    private static final long serialVersionUID = 3481528528238978452L;

    private Integer code;
    private String msg;

    private T data;

    private SingleResult(T data){
        code = 0;
        msg = "success";
        this.data = data;
    }

    private SingleResult(T data, String msg){
        code = 0;
        this.msg = msg;
        this.data = data;
    }

    private SingleResult(CodeMsg codeMsg){
        if(codeMsg != null){
            code = codeMsg.getCode();
            msg = codeMsg.getMsg();
        }
    }

    public static <T> SingleResult<T> success(T data){
        return new SingleResult<>(data);
    }

    public static <T> SingleResult<T> error(CodeMsg codeMsg){
        return new SingleResult<>(codeMsg);
    }

    public static <T> SingleResult<T> success(T data, String msg){
        return new SingleResult<>(data,msg);
    }

}
