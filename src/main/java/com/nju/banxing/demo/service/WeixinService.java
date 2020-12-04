package com.nju.banxing.demo.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.common.wx.WxSessionInfo;
import com.nju.banxing.demo.common.wx.WxUserInfo;
import com.nju.banxing.demo.config.WxMaServiceFactory;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.request.WxPayOrderRequest;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.vo.WxPayOrderVO;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 发送小程序通知
     * @return
     */
    public boolean sendWxMessage(String userOpenid, String templateId, String page, List<WxMaSubscribeMessage.Data> dataList){
        final WxMaService wxMaService = WxMaServiceFactory.getWxMaService();
        WxMaSubscribeMessage message = new WxMaSubscribeMessage();
        message.setData(dataList);
        message.setToUser(userOpenid);
        message.setTemplateId(templateId);
        message.setPage(page);

        try {
            wxMaService.getMsgService().sendSubscribeMsg(message);
            return true;
        } catch (WxErrorException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 给用户下发腾讯会议通知
     * @param meetTitle
     * @param date
     * @param teacherName
     * @param meetNum
     * @param tips
     * @return
     */
    public List<WxMaSubscribeMessage.Data> getWxMeetMessage(String meetTitle, String date, String teacherName, String meetNum, String tips){
        Map<String,String> map = Maps.newHashMap();
        map.put("thing1",meetTitle);
        map.put("time2",date);
        map.put("thing3",teacherName);
        map.put("character_string4",meetNum);
        map.put("thing5",tips);
        return map.entrySet().stream().map(
                m -> new WxMaSubscribeMessage.Data(m.getKey(),m.getValue())
        ).collect(Collectors.toList());
    }

    /**
     * 微信支付统一下单
     * @throws WxPayException
     */
    public WxPayOrderVO createPayOrder(WxPayOrderRequest orderRequest) throws WxPayException {
        WxPayService wxPayService = WxMaServiceFactory.getWxPayService();
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();

        request.setOpenid(orderRequest.getOpenid());
        request.setBody(orderRequest.getBody());
        request.setDetail(orderRequest.getDetail());
        request.setOutTradeNo(orderRequest.getOutTradeNo());
        request.setTotalFee(orderRequest.getTotalTee());
        request.setSpbillCreateIp(orderRequest.getIp());
        request.setNonceStr(orderRequest.getNonceStr());
        request.setNotifyUrl(orderRequest.getNotifyUrl());
        request.setTradeType(orderRequest.getTradeType());
        request.setTimeStart(DateUtil.now().format(DateUtil.wxPayFormatter));
        request.setTimeExpire(DateUtil.now().plusMinutes(30).format(DateUtil.wxPayFormatter));

        WxPayMpOrderResult result = wxPayService.createOrder(request);
        WxPayOrderVO vo = new WxPayOrderVO();
        vo.setNonceStr(result.getNonceStr());
        vo.setPackageValue(result.getPackageValue());
        vo.setPaySign(result.getPaySign());
        vo.setSignType(result.getSignType());
        vo.setTimeStamp(result.getTimeStamp());
        return vo;

    }

    public WxPayOrderNotifyResult notifyOrderResult(String xml) throws WxPayException {
        WxPayService wxPayService = WxMaServiceFactory.getWxPayService();

        return wxPayService.parseOrderNotifyResult(xml);

    }


}
