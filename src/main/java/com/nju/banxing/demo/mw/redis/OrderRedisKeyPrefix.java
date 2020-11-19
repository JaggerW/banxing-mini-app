package com.nju.banxing.demo.mw.redis;

import com.nju.banxing.demo.config.AppContantConfig;

/**
 * @Author: jaggerw
 * @Description: 订单模块redisKey前缀
 * @Date: 2020/11/19
 */
public class OrderRedisKeyPrefix extends BaseRedisKeyPrefix {
    private OrderRedisKeyPrefix(String prefix) {
        super(prefix);
    }

    private OrderRedisKeyPrefix(Integer expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderRedisKeyPrefix dupKey = new OrderRedisKeyPrefix(AppContantConfig.DUP_KEY_EXPIRE_TIME,"DUP_KEY");
}
