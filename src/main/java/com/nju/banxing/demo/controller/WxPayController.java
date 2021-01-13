package com.nju.banxing.demo.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.sms.LoginVerSmsTemplate;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.config.WxMaConfig;
import com.nju.banxing.demo.enums.OrderStatusEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.exception.RetryException;
import com.nju.banxing.demo.service.*;
import com.nju.banxing.demo.util.MathUtil;
import com.nju.banxing.demo.vo.AliyunSmsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @Author: jaggerw
 * @Description: 微信支付
 * @Date: 2020/12/19
 */
@RestController
@RequestMapping("/pay")
@Slf4j
public class WxPayController {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AliyunService aliyunService;

    @Autowired
    private PayService payService;

    @PostMapping("/cancel")
    @MethodLog("取消微信支付")
    public SingleResult<Boolean> cancelPay(String openid,
                                           @RequestParam("orderCode") String orderCode){

        boolean b = payService.cancelPay(orderCode, openid);
        return SingleResult.success(b);

    }

    @PostMapping("/order_notify")
    @MethodLog("微信支付回调方法")
    public String parseOrderNotifyResult(@RequestBody String xmlData) {
        try {
            WxPayOrderNotifyResult result = weixinService.notifyOrderResult(xmlData);
            checkPayResult(result);
            // 判断是否已处理过
            Integer status = orderService.getStatusByCode(result.getOutTradeNo());
            if(OrderStatusEnum.ORDER_TO_PAY.getCode().equals(status) ||
                    OrderStatusEnum.ORDER_FAIL_PAY.getCode().equals(status)){

                if(OrderStatusEnum.ORDER_FAIL_PAY.getCode().equals(status) && "FAIL".equals(result.getResultCode())){
                    // 已处理过
                    return WxPayNotifyResponse.success("成功");
                }

                // 验证金额是否一致防止伪造通知
                boolean b = checkPayCost(result);
                if(!b){
                    log.error("支付金额校验错误");
                    return WxPayNotifyResponse.fail("支付金额校验错误");
                }

                // 处理
                if("SUCCESS".equals(result.getResultCode())){
                    // 支付成功
                    payService.successPay(result);

                    // TODO 短信通知导师，需要企业申请
//                    String verCode = "123456";
//                    String mobile = orderService.getTutorMobileByOrderCode(result.getOutTradeNo());
//                    // 阿里云发送短信
//                    AliyunSmsVO aliyunSmsVO = new AliyunSmsVO();
//                    aliyunSmsVO.setPhoneNumber(mobile);
//                    aliyunSmsVO.setSignName(AppContantConfig.ALIYUN_SMS_SIGN_NAME);
//                    aliyunSmsVO.setTemplateCode(AppContantConfig.ALIYUN_SMS_LOGIN_VERIFICATION_TEMPLATE_CODE);
//
//                    LoginVerSmsTemplate template = new LoginVerSmsTemplate();
//                    template.setCode(verCode);
//                    aliyunSmsVO.setTemplateParam(JSON.toJSONString(template));
//                    aliyunService.sendSMS(aliyunSmsVO);
                }else {
                    // 支付失败
                    payService.failPay(result);
                }
            }
            return WxPayNotifyResponse.success("成功");
        } catch (RetryException e) {
            throw e;
        } catch (WxPayException e) {
            e.printStackTrace();
            return WxPayNotifyResponse.fail("参数校验错误");
        } catch (GlobalException e){
            log.error(e.getCodeMsg().getMsg());
            return WxPayNotifyResponse.fail("参数校验错误");
        }
    }

    @PostMapping("/refund_notify")
    @MethodLog("微信退款回调方法")
    public String parseRefundNotifyResult(@RequestBody String xmlDate){
        try {
            WxPayRefundNotifyResult result = weixinService.notifyRefundResult(xmlDate);
            if(ObjectUtils.isEmpty(result) || !"SUCCESS".equals(StringUtils.trimToEmpty(result.getReturnCode().toUpperCase()))){
                log.error("微信退款申请回调结果为空！");
                throw new WxPayException("回调返回体为空");
            }
            final String refundStatus = StringUtils.trimToEmpty(result.getReqInfo().getRefundStatus().toUpperCase());
            if("SUCCESS".equals(refundStatus)){
                // 退款成功
                payService.successRefund(result);
            }
            else if ("CHANGE".equals(refundStatus)){
                // 退款异常
                payService.failRefund(result);
            }

            return WxPayNotifyResponse.success("成功");
        } catch (WxPayException e) {
            e.printStackTrace();
            return WxPayNotifyResponse.fail("参数校验错误");
        } catch (GlobalException e){
            log.error(e.getCodeMsg().getMsg());
            return WxPayNotifyResponse.fail("业务处理失败");
        }
    }

    private void checkPayResult(WxPayOrderNotifyResult result){
        if(StringUtils.isNotEmpty(result.getReturnMsg())){
            log.error(result.getReturnMsg());
            throw new GlobalException(CodeMsg.FAIL_PAY_ERROR_SIGN);
        }

        if("FAIL".equals(result.getReturnCode())){
            log.error("returnCode is fail");
            throw new GlobalException(CodeMsg.FAIL_PAY_ERROR_COM);
        }
    }

    private boolean checkPayCost(WxPayOrderNotifyResult result){
        Integer totalFee = result.getTotalFee();
        String orderCode = result.getOutTradeNo();
        BigDecimal totalCost = orderService.getTotalCostByCode(orderCode);
        int calFee = MathUtil.bigYuan2Fee(totalCost);
        log.info("微信支付回调金额为：{}; 数据库订单中支付金额为：{}",totalFee,calFee);
        return totalFee.equals(calFee);
    }


}
