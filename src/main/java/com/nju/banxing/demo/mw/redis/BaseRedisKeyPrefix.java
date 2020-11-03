package com.nju.banxing.demo.mw.redis;


/**
 * @Author: jaggerw
 * @Description: redisKey前缀基类
 * @Date: 2020/11/3
 */
public abstract class BaseRedisKeyPrefix implements RedisKeyPrefix {

    private Integer expireSeconds;
    private String prefix;

    protected BaseRedisKeyPrefix(String prefix){
        this.expireSeconds = 0;  //默认永不过期
        this.prefix = prefix;
    }

    protected BaseRedisKeyPrefix(Integer expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public Integer getExpireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        return getClass().getSimpleName() + ":" + prefix;
    }
}
