package com.nju.banxing.demo.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.nju.banxing.demo.common.wx.WxMaSessionInfo;
import com.nju.banxing.demo.config.WxMaServiceFactory;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
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
    public WxMaSessionInfo login(String code) {
        final WxMaService wxMaService = WxMaServiceFactory.getWxMaService();
        try {
            WxMaJscode2SessionResult result = wxMaService.jsCode2SessionInfo(code);
            if(result != null){
                log.info("获取微信session信息成功");
                return new WxMaSessionInfo(result.getOpenid(),result.getSessionKey());
            }
        } catch (WxErrorException e) {
            log.info(e.getMessage());
            log.info(e.toString());
            e.printStackTrace();
        }
        return null;
    }


}
