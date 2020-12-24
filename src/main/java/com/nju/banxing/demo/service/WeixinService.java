package com.nju.banxing.demo.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
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
import com.nju.banxing.demo.request.WxRefundRequest;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.vo.WxPayOrderVO;
import com.nju.banxing.demo.vo.WxRefundVO;
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
    public WxUserInfo getUserInfo(String sessionKey, String signature, String rawData, String encryptedData, String iv) {
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
    public boolean sendWxMessage(String targetUserId, String templateId, String page, List<WxMaSubscribeMessage.Data> dataList){
        log.info("===== BEGIN SEND WX MESSAGE");
        final WxMaService wxMaService = WxMaServiceFactory.getWxMaService();
        WxMaSubscribeMessage message = new WxMaSubscribeMessage();
        message.setData(dataList);
        message.setToUser(targetUserId);
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
     * 预约成功通知
     * @param meetTitle
     * @param date
     * @param meetID
     * @param meetSecret
     * @return
     */
    public List<WxMaSubscribeMessage.Data> getMeetingSuccessWxMessage(String meetTitle, String date, String meetID, String meetSecret){
        Map<String,String> map = Maps.newHashMap();
        map.put("thing23",meetTitle);
        map.put("time24",date);
        map.put("character_string25",meetID);
        map.put("character_string26",meetSecret);
        map.put("thing15","请按时登录腾讯会议进行咨询");
        return map.entrySet().stream().map(
                m -> new WxMaSubscribeMessage.Data(m.getKey(),m.getValue())
        ).collect(Collectors.toList());
    }

    /**
     * 预约取消通知
     * @param rejectReason
     * @return
     */
    public List<WxMaSubscribeMessage.Data> getMeetingRejectWxMessage(String rejectReason){
        Map<String,String> map = Maps.newHashMap();
        map.put("thing11",rejectReason);
        map.put("thing4","订单已付款将于1-3日内按原路经退回");
        return map.entrySet().stream().map(
                m -> new WxMaSubscribeMessage.Data(m.getKey(),m.getValue())
        ).collect(Collectors.toList());
    }

    /**
     * 导师申请审核结果通知
     * @param applyResult
     * @param tips
     * @return
     */
    public List<WxMaSubscribeMessage.Data> getTutorApplyResultWxMessage(String applyResult, String tips){
        Map<String,String> map = Maps.newHashMap();
        map.put("phrase2",applyResult);
        map.put("thing3",tips);
        return map.entrySet().stream().map(
                m -> new WxMaSubscribeMessage.Data(m.getKey(),m.getValue())
        ).collect(Collectors.toList());
    }

    /**
     * 微信支付统一下单
     * @throws WxPayException
     */
    public WxPayOrderVO createPayOrder(WxPayOrderRequest orderRequest) throws WxPayException {
        log.info("===== BEGIN WX CREATE PAY ORDER");
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

    /**
     * 微信支付回调
     * @param xml
     * @return
     * @throws WxPayException
     */
    public WxPayOrderNotifyResult notifyOrderResult(String xml) throws WxPayException {
        log.info("===== BEGIN RESOLVE PAY ORDER NOTIFY RESULT");

        WxPayService wxPayService = WxMaServiceFactory.getWxPayService();

        return wxPayService.parseOrderNotifyResult(xml);

    }

    /**
     * 申请微信退款
     * @param refundRequest
     * @return
     * @throws WxPayException
     */
    public WxRefundVO applyRefund(WxRefundRequest refundRequest) throws WxPayException {
        log.info("===== BEGIN WX PAY APPLY REFUND");
        WxPayService wxPayService = WxMaServiceFactory.getWxPayService();

        WxPayRefundRequest request = new WxPayRefundRequest();
        request.setNonceStr(refundRequest.getNonceStr());
        request.setNotifyUrl(refundRequest.getNotifyUrl());
        request.setRefundDesc(refundRequest.getRefundDesc());
        request.setOutTradeNo(refundRequest.getOrderCode());
        request.setOutRefundNo(refundRequest.getOrderRefundCode());
        request.setRefundFee(refundRequest.getRefundFee());
        request.setTotalFee(refundRequest.getTotalFee());

        WxPayRefundResult result = wxPayService.refund(request);

        WxRefundVO vo = new WxRefundVO();
        vo.setOrderCode(result.getOutTradeNo());
        vo.setOrderRefundCode(result.getOutRefundNo());
        vo.setTransactionId(result.getTransactionId());
        vo.setRefundId(result.getRefundId());
        vo.setRefundFee(result.getRefundFee());
        return vo;
    }

    /**
     * 微信退款回调
     * @param xml
     * @return
     * @throws WxPayException
     */
    public WxPayRefundNotifyResult notifyRefundResult(String xml) throws WxPayException {
        log.info("===== BEGIN RESOLVE ORDER REFUND NOTIFY RESULT");

        WxPayService wxPayService = WxMaServiceFactory.getWxPayService();

        return wxPayService.parseRefundNotifyResult(xml);
    }



}
