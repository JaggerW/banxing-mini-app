package com.nju.banxing.demo.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.fastjson.JSON;
import com.nju.banxing.demo.common.wx.WxSessionInfo;
import com.nju.banxing.demo.common.wx.WxUserInfo;
import com.nju.banxing.demo.config.WxMaServiceFactory;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @Author: jaggerw
 * @Description: 微信服务类
 * @Date: 2020/11/3
 */
@Service
@Slf4j
public class WeixinService {

    /**
     * 登录
     * @param code
     * @return
     */
    public WxSessionInfo login(String code) {
        final WxMaService wxMaService = WxMaServiceFactory.getWxMaService();
        try {
            WxMaJscode2SessionResult result = wxMaService.jsCode2SessionInfo(code);
            if(result != null){
                log.info("获取微信session信息成功");
                return new WxSessionInfo(result.getOpenid(),result.getSessionKey());
            }
        } catch (WxErrorException e) {
            log.info(e.getMessage());
            log.info(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户信息
     * @param sessionKey
     * @param signature
     * @param rawData
     * @param encryptedData
     * @param iv
     * @return
     * @throws Exception
     */
    public WxUserInfo getUserInfo(String sessionKey, String signature, String rawData, String encryptedData, String iv) throws Exception{
        log.info("获取微信用户信息,sessionKey:{},signature:{},rawData:{},encryptedData:{},iv:{}",
                sessionKey,signature,rawData,encryptedData,iv);
        final WxMaService wxMaService = WxMaServiceFactory.getWxMaService();

        // 校验用户信息
        if(!wxMaService.getUserService().checkUserInfo(sessionKey,rawData,signature)){
            throw new GlobalException(CodeMsg.WX_ERROR_CHECK_USER_INFO);
        }

        // 解密用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        if(ObjectUtils.isEmpty(userInfo)){
            throw new GlobalException(CodeMsg.WX_ERROR_GET_USER_INFO);
        }
        WxUserInfo innerUserInfo = new WxUserInfo();
        BeanUtils.copyProperties(userInfo,innerUserInfo);
        log.info("微信用户信息为：{}", JSON.toJSONString(innerUserInfo));
        return innerUserInfo;

    }


}
