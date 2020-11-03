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

    // aliyun request
    public final static String ALIYUN_REQUEST_DOMAIN ="dysmsapi.aliyuncs.com";
    public final static String ALIYUN_REQUEST_VERSION = "2017-05-25";
    public final static String ALIYUN_REQUEST_SENDSMS_ACTION = "SendSms";

    // aliyun sms
    public final static String ALIYUN_SMS_SIGN_NAME = "伴行";
    public final static String ALIYUN_SMS_TEMPLATE_CODE = "SMS_205133162";

    // aliyun oss
    public final static String ALIYUN_OSS_END_POINT = "http://oss-cn-beijing.aliyuncs.com";
    public final static String ALIYUN_OSS_BUCKET = "jaggerw-second-test";
    public final static String ALIYUN_OSS_IMAGES_FOLDER = "images";
    public final static String ALIYUN_OSS_PDF_FOLDER = "pdf";
    public final static String ALIYUN_OSS_URL_PREFIX = "https://codingboy.top/";


}
