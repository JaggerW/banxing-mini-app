package com.nju.banxing.demo.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: app固定配置类
 * @Date: 2020/11/3
 */
@Data
public class AppContantConfig implements Serializable {
    private static final long serialVersionUID = 7065824165918580319L;

    // aliyun sms
    public final static String ALIYUN_REQUEST_SENDSMS_DOMAIN ="dysmsapi.aliyuncs.com";
    public final static String ALIYUN_REQUEST_SENDSMS_VERSION = "2017-05-25";
    public final static String ALIYUN_REQUEST_SENDSMS_ACTION = "SendSms";
    public final static String ALIYUN_REGION_ID = "cn-hangzhou";
    public final static String ALIYUN_LOGIN_VERIFICATION_SMS_SIGN_NAME = "伴行";
    public final static String ALIYUN_LOGIN_VERIFICATION_SMS_TEMPLATE_CODE = "SMS_205133162";

    // aliyun oss
    public final static String ALIYUN_OSS_END_POINT = "http://oss-cn-beijing.aliyuncs.com";
    public final static String ALIYUN_OSS_BUCKET = "jaggerw-second-test";
    public final static String ALIYUN_OSS_IMAGES_FOLDER = "images/";
    public final static String ALIYUN_OSS_PDF_FOLDER = "pdf/";
    public final static String ALIYUN_OSS_URL_PREFIX = "https://codingboy.top/";

    // redis expireTime
    public final static Integer USER_TOKEN_EXPIRE_TIME = 24*3600;
    public final static Integer VER_CODE_EXPIRE_TIME = 300;
    public final static Integer DUP_KEY_EXPIRE_TIME = 1800;

    // 用户token放入url传输中的参数名称
    public static final String USER_TOKEN_HEADER_NAME = "userToken";
    public static final String USER_TOKEN_PARAM_NAME = "token";
    public static final String OPEN_ID_PARAM_NAME = "openid";

    // 不需要校验用户token的路径
    public static final String[] IGNORE_TOKEN_PATH = {"/user/login", "/test",
            "/home/tutor_list","/home/comment_list","/home/tutor_detail",
            "/order/notify"};

    public static final String SERVER_PATH_PREFIX = "https://www.codingboy.top/banxing";

}
