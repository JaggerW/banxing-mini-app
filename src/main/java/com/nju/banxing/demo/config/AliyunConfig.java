package com.nju.banxing.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 读取阿里云配置文件
 * @Date: 2020/11/3
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.accesskey")
public class AliyunConfig implements Serializable {
    private static final long serialVersionUID = -6791419384599249707L;

    private String id;
    private String secret;
}
