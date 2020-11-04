package com.nju.banxing.demo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: jaggerw
 * @Description: 加载redis配置
 * @Date: 2020/11/3
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Slf4j
public class RedisConfig {

    private String host;

    private Integer port;

    private String password;

    private Integer timeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private Integer maxActive;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private Integer maxIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private Integer maxWait;

    @Bean
    public JedisPool createJedisPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        return new JedisPool(jedisPoolConfig,host,port,timeout,password,0);
    }

}
