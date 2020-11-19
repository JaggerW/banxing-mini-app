package com.nju.banxing.demo.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sound.midi.Soundbank;

/**
 * @Author: jaggerw
 * @Description: 微信配置
 * @Date: 2020/11/3
 */
@Slf4j
@Component
public class WxMaServiceFactory {

    @Autowired
    private WxMaConfig wxMaConfig;

    private static WxMaService wxMaService;

    private static WxPayService wxPayService;

    public static WxMaService getWxMaService() {
        if (wxMaService == null) {
            throw new IllegalArgumentException("未找到weixin-miniapp-wxmaservice的配置，请核实！");
        }
        return wxMaService;
    }

    public static WxPayService getWxPayService() {
        if (wxPayService == null) {
            throw new IllegalArgumentException("未找到weixin-miniapp-wxpayservice的配置，请核实！");
        }
        return wxPayService;
    }


    @PostConstruct
    public void init(){
        if(wxMaConfig == null){
            throw new IllegalArgumentException("未找到weixin-miniapp-properties的配置，请核实！");
        }

        WxMaDefaultConfigImpl wxMaDefaultConfig = new WxMaDefaultConfigImpl();
        wxMaDefaultConfig.setAppid(wxMaConfig.getAppid());
        wxMaDefaultConfig.setSecret(wxMaConfig.getSecret());

        wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaDefaultConfig);

        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(wxMaConfig.getAppid()));
        payConfig.setMchId(StringUtils.trimToNull(wxMaConfig.getMchid()));
        payConfig.setMchKey(StringUtils.trimToNull(wxMaConfig.getMchkey()));
//        payConfig.setSubAppId(StringUtils.trimToNull(this.properties.getSubAppId()));
//        payConfig.setSubMchId(StringUtils.trimToNull(this.properties.getSubMchId()));
        payConfig.setKeyPath(StringUtils.trimToNull(wxMaConfig.getKeypath()));

        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);

        wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);

    }
}
