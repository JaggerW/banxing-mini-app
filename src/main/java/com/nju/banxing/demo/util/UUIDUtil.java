package com.nju.banxing.demo.util;


import java.util.UUID;

/**
 * @Author: jaggerw
 * @Description: uuid工具类
 * @Date: 2020/11/4
 */
public class UUIDUtil {
    private static String[] nums = new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };
    private static String[] chars = new String[]{
            "a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成验证码
     *
     * @return
     */
    public static String getVerCode() {
        StringBuffer code = new StringBuffer();
        String uuid = getUUID();
        for (int i = 0; i < 6; ++i) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int n = Integer.parseInt(str, 16);
            code.append(nums[n % 0x0A]);
        }
        return code.toString();
    }

    /**
     * 生成userToken
     *
     * @return
     */
    public static String getUserToken() {
        StringBuffer code = new StringBuffer();
        String uuid = getUUID();
        for (int i = 0; i < 16; ++i) {
            String str = uuid.substring(i * 2, i * 2 + 2);
            int n = Integer.parseInt(str, 16);
            code.append(chars[n % 0x3E]);
        }
        return code.toString();
    }

    /**
     * 生成订单号
     *
     * @return
     */
    public static String getOrderCode() {
        long timeStr = System.currentTimeMillis();
        return "DD" + timeStr + getVerCode();
    }

    public static String getImageFileName(){
        long timeStr = System.currentTimeMillis();
        return "image_" + timeStr + getVerCode();
    }

    public static String getPdfFileName(){
        long timeStr = System.currentTimeMillis();
        return "pdf_" + timeStr + getVerCode();
    }
}
