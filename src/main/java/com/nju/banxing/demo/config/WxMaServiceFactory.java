package com.nju.banxing.demo.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: jaggerw
 * @Description: TODO
 * @Date: 2020/11/3
 */
@Slf4j
@Component
public class WxMaServiceFactory {

    @Autowired
    private WxMaConfig wxMaConfig;

    private static WxMaService wxMaService;

    public static WxMaService getWxMaService() {
        if (wxMaService == null) {
            throw new IllegalArgumentException("未找到weixin-miniapp-wxmaservice的配置，请核实！");
        }
        return wxMaService;
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
    }
}
