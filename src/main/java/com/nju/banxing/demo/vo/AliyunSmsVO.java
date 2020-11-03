package com.nju.banxing.demo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 阿里云短信
 * @Date: 2020/11/3
 */
@Data
public class AliyunSmsVO implements Serializable {
    private static final long serialVersionUID = 6545378793447557797L;

    private String phoneNumber;
    private String signName;
    private String templateCode;
    private String outId;
    private String templateParam;

}
