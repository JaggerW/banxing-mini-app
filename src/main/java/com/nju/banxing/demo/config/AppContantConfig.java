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
    public final static String ALIYUN_SMS_SIGN_NAME = "伴行";
    public final static String ALIYUN_SMS_LOGIN_VERIFICATION_TEMPLATE_CODE = "SMS_205133162";
    public final static String ALIYUN_SMS_NEW_ORDER_TEMPLATE_CODE = "";

    // aliyun oss
    public final static String ALIYUN_OSS_END_POINT = "http://oss-cn-beijing.aliyuncs.com";
    public final static String ALIYUN_OSS_BUCKET = "jaggerw-second-test";
    public final static String ALIYUN_OSS_IMAGES_FOLDER = "images/";
    public final static String ALIYUN_OSS_PDF_FOLDER = "pdf/";
    public final static String ALIYUN_OSS_URL_PREFIX = "https://codingboy.top/";

    // weixin msg
    public final static String WX_MSG_MEETING_SUCCESS_TEMPLATE_ID = "-KzAdRPopRCtVe-bd2maIwcoC-DGwtgzqHwH7Zhs4bk";
    public final static String WX_MSG_MEETING_SUCCESS_PAGE = "";
    public final static String WX_MSG_MEETING_REJECT_TEMPLATE_ID = "xpTRepIRyQYmr80mHvjWr0IbgIlF8YG7ZjAsLa7xhfo";
    public final static String WX_MSG_MEETING_REJECT_PAGE = "";
    public final static String WX_MSG_TUTOR_APPLY_TEMPLATE_ID = "M0AQDZB4g9kxKeyYxItD5Fo1tAexqPXyNEu7zeQVsOE";
    public final static String WX_MSG_TUTOR_APPLY_PAGE = "";


    // redis expireTime
    public final static Integer USER_TOKEN_EXPIRE_TIME = 24*3600;
    public final static Integer VER_CODE_EXPIRE_TIME = 300;
    public final static Integer DUP_KEY_EXPIRE_TIME = 300;

    // 用户token放入url传输中的参数名称
    public static final String USER_TOKEN_HEADER_NAME = "userToken";
    public static final String USER_TOKEN_PARAM_NAME = "token";
    public static final String OPEN_ID_PARAM_NAME = "openid";

    // 不需要校验用户token的路径
    public static final String[] IGNORE_TOKEN_PATH = {"/user/login", "test",
            "/home/tutor_list","/comment/list","/home/tutor_detail",
            "/pay/order_notify","/pay/refund_notify"};

    public static final String SERVER_PATH_PREFIX = "https://www.codingboy.top/banxing";

}
