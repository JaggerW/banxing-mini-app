package com.nju.banxing.demo.mw.redis;

import com.nju.banxing.demo.config.AppContantConfig;

/**
 * @Author: jaggerw
 * @Description: 用户模块redisKey前缀类
 * @Date: 2020/11/3
 */
public class UserRedisKeyPrefix extends BaseRedisKeyPrefix {
    private UserRedisKeyPrefix(String prefix) {
        super(prefix);
    }

    private UserRedisKeyPrefix(Integer expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserRedisKeyPrefix userToken = new UserRedisKeyPrefix(AppContantConfig.USER_TOKEN_EXPIRE_TIME,"USER_TOKEN");

    public static UserRedisKeyPrefix userOpenId = new UserRedisKeyPrefix(AppContantConfig.USER_TOKEN_EXPIRE_TIME, "OPEN_ID");
}
