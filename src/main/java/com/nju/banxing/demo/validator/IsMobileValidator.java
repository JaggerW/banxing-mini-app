package com.nju.banxing.demo.validator;

import com.nju.banxing.demo.annotation.IsMobile;
import com.nju.banxing.demo.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author: jaggerw
 * @Description: 移动手机号码校验类
 * @Date: 2020/11/3
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(!required && StringUtils.isEmpty(s)){
            return true;
        }else {
            return ValidatorUtil.isMobile(s);
        }
    }
}
