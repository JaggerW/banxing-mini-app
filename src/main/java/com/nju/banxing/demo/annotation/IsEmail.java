package com.nju.banxing.demo.annotation;

import com.nju.banxing.demo.validator.IsEmailValidator;
import com.nju.banxing.demo.validator.IsMobileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author: jaggerw
 * @Description: 验证邮箱注解
 * @Date: 2020/11/5
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {IsEmailValidator.class}
)
public @interface IsEmail {
    // email是否可以为空，默认是不能为空
    boolean required() default true;

    String message() default "邮箱格式错误!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
