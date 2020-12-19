package com.nju.banxing.demo.util;

import java.math.BigDecimal;

/**
 * @Author: jaggerw
 * @Description: 数学工具
 * @Date: 2020/12/1
 */
public class MathUtil {

    public static int bigYuan2Fee(BigDecimal yuan){
        return yuan.setScale(2, 4).multiply(new BigDecimal(100)).intValue();
    }

    public static BigDecimal fee2BigYuan(Integer fee){
        float yuan = (float) fee / 100;
        return new BigDecimal(yuan).setScale(2,4);
    }
}
