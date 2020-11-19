package com.nju.banxing.demo.service;

import com.alibaba.fastjson.JSON;
import com.nju.banxing.demo.mw.redis.RedisKeyPrefix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Author: jaggerw
 * @Description: redis服务类
 * @Date: 2020/11/3
 */
@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;


    /**
     * 获取单个对象
     *
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(RedisKeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            return JSON.parseObject(str, clazz);
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     * 设置对象
     *
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> Boolean set(RedisKeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = JSON.toJSONString(value);
            if (StringUtils.isEmpty(str)) {
                return false;
            }
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.getExpireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, seconds, str);
            }
            return true;
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     * 判断是否存在该KEY
     *
     * @param prefix
     * @param key
     * @return
     */
    public Boolean exists(RedisKeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     * setnx + setex 注意！非原子操作
     *
     * @param prefix
     * @param key
     * @param value
     * @return
     */
    public <T> Boolean setnx(RedisKeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = JSON.toJSONString(value);
            if (StringUtils.isEmpty(str)) {
                return false;
            }
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.getExpireSeconds();
            Long res = jedis.setnx(realKey, str);
            if(seconds > 0 && res > 0){
                jedis.expire(realKey,seconds);
                return true;
            }else {
                return res > 0;
            }
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     * 删除该KEY
     * @param prefix
     * @param key
     * @return
     */
    public Boolean delete(RedisKeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.del(realKey) > 0;
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     * 更新key的有效期
     * @param prefix
     * @param key
     * @return
     */
    public Boolean updateExpire(RedisKeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.expire(realKey, prefix.getExpireSeconds()) > 0;
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     * 更新key的有效期
     * @param prefix
     * @param key
     * @return
     */
    public Boolean updateExpire(RedisKeyPrefix prefix, String key, Integer seconds){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.expire(realKey, seconds) > 0;
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     * 原子操作，自增1
     *
     * @param prefix
     * @param key
     * @return
     */
    public Long incr(RedisKeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     * 原子操作，自减1
     *
     * @param prefix
     * @param key
     * @return
     */
    public Long decr(RedisKeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            return2Pool(jedis);
        }
    }

    private void return2Pool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
