package com.nju.banxing.demo.validator;

import com.nju.banxing.demo.annotation.IsEmail;
import com.nju.banxing.demo.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author: jaggerw
 * @Description: 邮箱格式校验
 * @Date: 2020/11/5
 */
public class IsEmailValidator implements ConstraintValidator<IsEmail,String> {

    private boolean required;

    @Override
    public void initialize(IsEmail constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(!required && StringUtils.isEmpty(s)){
            return true;
        }else {
            return ValidatorUtil.isEmail(s);
        }
    }
}
