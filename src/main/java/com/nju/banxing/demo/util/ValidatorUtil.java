package com.nju.banxing.demo.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: jaggerw
 * @Description: 校验类
 * @Date: 2020/11/3
 */
public class ValidatorUtil {
    private static final Pattern mobilePattern = Pattern.compile("1[35678]\\d{9}");

    private static final Pattern emailPattern = Pattern.compile("^([A-Za-z0-9_\\-.])+@([A-Za-z0-9_\\-.])+\\.([A-Za-z]{2,4})$");

    /**
     * 判断是否符合手机号格式
     *
     * @param inputStr
     * @return
     */
    public static boolean isMobile(String inputStr) {
        if (StringUtils.isEmpty(inputStr)) {
            return false;
        }
        Matcher m = mobilePattern.matcher(inputStr);
        return m.matches();
    }

    public static boolean isEmail(String inputStr){
        if (StringUtils.isEmpty(inputStr)) {
            return false;
        }
        Matcher m = emailPattern.matcher(inputStr);
        return m.matches();
    }

}
