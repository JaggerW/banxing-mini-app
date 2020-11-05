package com.nju.banxing.demo.exception;

import com.nju.banxing.demo.common.SingleResult;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 全局异常处理类
 * @Date: 2020/11/5
 */
@RestControllerAdvice
public class GlobalExceptionHandle {
    @ExceptionHandler(value=Exception.class)
    public SingleResult<String> exceptionHandler(HttpServletRequest request, Exception e){
        e.printStackTrace();
        if(e instanceof GlobalException) {
            GlobalException ex = (GlobalException)e;
            return SingleResult.error(ex.getCodeMsg());
        }else if(e instanceof BindException) {
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return SingleResult.error(CodeMsg.PARAM_ERROR.fillArgs(msg));
        }else {
            return SingleResult.error(CodeMsg.SERVER_ERROR);
        }
    }
}
