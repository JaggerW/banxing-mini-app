package com.nju.banxing.demo.exception;

import com.nju.banxing.demo.common.SingleResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 全局异常处理类
 * @Date: 2020/11/5
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {
    @ExceptionHandler(value=Exception.class)
    public SingleResult<String> exceptionHandler(Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
        if(e instanceof GlobalException) {
            GlobalException ex = (GlobalException)e;
            log.error(ex.getCodeMsg().getMsg());
            return SingleResult.error(ex.getCodeMsg());
        }else if(e instanceof BindException) {
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return SingleResult.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else if(e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = ex.getBindingResult();
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            ObjectError objectError = allErrors.get(0);
            String defaultMessage = objectError.getDefaultMessage();
            return SingleResult.error(CodeMsg.BIND_ERROR.fillArgs(defaultMessage));
        } else {
            return SingleResult.error(CodeMsg.SERVER_ERROR);
        }
    }
}
