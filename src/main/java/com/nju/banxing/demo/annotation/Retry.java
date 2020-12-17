package com.nju.banxing.demo.annotation;

import java.lang.annotation.*;

/**
 * @Author: jaggerw
 * @Description: 乐观锁重试机制
 * @Date: 2020/12/17
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retry {
}
