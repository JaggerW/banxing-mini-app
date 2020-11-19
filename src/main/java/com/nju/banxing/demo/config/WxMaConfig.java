package com.nju.banxing.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 读取微信配置文件
 * @Date: 2020/11/3
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "weixin.miniapp")
public class WxMaConfig implements Serializable {
    private static final long serialVersionUID = 284090659678487192L;

    private String appid;
    private String secret;

    private String mchid;
    private String mchkey;
    private String keypath;
}
