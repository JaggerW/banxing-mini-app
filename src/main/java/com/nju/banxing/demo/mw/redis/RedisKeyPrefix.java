package com.nju.banxing.demo.mw.redis;

/**
 * @Author: jaggerw
 * @Description: redisKey前缀
 * @Date: 2020/11/3
 */
public interface RedisKeyPrefix {
    Integer getExpireSeconds();

    String getPrefix();
}
