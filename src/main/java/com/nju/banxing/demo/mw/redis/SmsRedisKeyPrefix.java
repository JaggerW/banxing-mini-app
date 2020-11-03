package com.nju.banxing.demo.mw.redis;

import com.nju.banxing.demo.config.AppContantConfig;

/**
 * @Author: jaggerw
 * @Description: 短信模块redisKey前缀类
 * @Date: 2020/11/3
 */
public class SmsRedisKeyPrefix extends BaseRedisKeyPrefix {
    private SmsRedisKeyPrefix(String prefix) {
        super(prefix);
    }

    private SmsRedisKeyPrefix(Integer expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SmsRedisKeyPrefix verCode = new SmsRedisKeyPrefix(AppContantConfig.VER_CODE_EXPIRE_TIME,"VER_CODE");

}
