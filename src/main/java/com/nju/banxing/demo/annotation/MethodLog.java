package com.nju.banxing.demo.annotation;

import java.lang.annotation.*;

/**
 * @Author: jaggerw
 * @Description: 日志注解类
 * @Date: 2020/11/3
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodLog {

    String value() default "default description";

}