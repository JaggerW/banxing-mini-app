package com.nju.banxing.demo.service;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.annotation.Retry;
import com.nju.banxing.demo.config.WxMaConfig;
import com.nju.banxing.demo.domain.CoinLogDO;
import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.domain.OrderLogDO;
import com.nju.banxing.demo.enums.CoinProcessTypeEnum;
import com.nju.banxing.demo.enums.OrderProcessTypeEnum;
import com.nju.banxing.demo.enums.OrderStatusEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.exception.RetryException;
import com.nju.banxing.demo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: jaggerw
 * @Description: 支付
 * @Date: 2020/12/21
 */
@Service
@Slf4j
public class PayService {


    @Autowired
    private OrderService orderService;

    @Autowired
    private WxMaConfig wxMaConfig;

    @Autowired
    private CoinService coinService;


    @Transactional
    @Retry
    public boolean failPay(WxPayOrderNotifyResult result) {
        String orderCode = result.getOutTradeNo();
        String openid = result.getOpenid();
        Map<String, Integer> map = orderService.getStatusAndVersionByCode(orderCode);
        if(ObjectUtils.isEmpty(map)){
            log.error("不存在该订单数据");
            return true;
        }
        Integer orderStatus = map.get("status");
        Integer version = map.get("version");
        if (OrderStatusEnum.ORDER_TO_PAY.getCode().equals(orderStatus)) {
            OrderStatusEnum nextOrderStatus = OrderStatusEnum.getEnumByCode(orderStatus).getNext(false);
            boolean update = orderService.updateOrderStatus(orderCode, nextOrderStatus.getCode(), version);
            if(update){
                throw new RetryException(CodeMsg.RETRY_ON_FAIL);
            }

            OrderLogDO orderLogDO = new OrderLogDO();
            orderLogDO.setId(UUIDUtil.getOrderLogCode());
            orderLogDO.setCreator(openid);
            orderLogDO.setModifier(openid);
            orderLogDO.setPreStatus(orderStatus);
            orderLogDO.setAfterStatus(nextOrderStatus.getCode());
            orderLogDO.setOrderCode(orderCode);
            orderLogDO.setProcessType(OrderProcessTypeEnum.FAIL.getCode());
            HashMap<Object, Object> errorMap = Maps.newHashMap();
            errorMap.put("wxPayOrderCode", result.getTransactionId());
            errorMap.put("errCode", result.getErrCode());
            errorMap.put("errDesc", result.getErrCodeDes());
            orderLogDO.setProcessContent("支付失败：" + JSON.toJSONString(errorMap));

            return orderService.insertOrderLog(orderLogDO);
        }
        return true;
    }

    @Transactional
    @Retry
    public boolean successPay(WxPayOrderNotifyResult result) {

        log.debug("===订单支付成功，开始更新数据===");

        String orderCode = result.getOutTradeNo();
        String openid = result.getOpenid();
        Map<String, Integer> map = orderService.getStatusAndVersionByCode(orderCode);
        if(ObjectUtils.isEmpty(map)){
            log.error("不存在该订单数据");
            return true;
        }
        Integer orderStatus = map.get("status");
        Integer version = map.get("version");

        if (OrderStatusEnum.ORDER_TO_PAY.getCode().equals(orderStatus) ||
                OrderStatusEnum.ORDER_FAIL_PAY.getCode().equals(orderStatus)) {

            // 更新订单状态
            OrderStatusEnum nextOrderStatus = OrderStatusEnum.getEnumByCode(orderStatus).getNext(true);
            boolean updateOrder = orderService.updateOrder4SuccessPay(orderCode, nextOrderStatus.getCode(), version);
            if(updateOrder){
                log.error("更新订单状态失败");
                throw new RetryException(CodeMsg.RETRY_ON_FAIL);
            }

            // 插入订单流水
            OrderLogDO orderLogDO = new OrderLogDO();
            orderLogDO.setId(UUIDUtil.getOrderLogCode());
            orderLogDO.setCreator(openid);
            orderLogDO.setModifier(openid);
            orderLogDO.setPreStatus(orderStatus);
            orderLogDO.setAfterStatus(nextOrderStatus.getCode());
            orderLogDO.setOrderCode(orderCode);
            orderLogDO.setProcessType(OrderProcessTypeEnum.SUCCESS.getCode());
            HashMap<Object, Object> successMap = Maps.newHashMap();
            successMap.put("wxPayOrderCode", result.getTransactionId());
            successMap.put("totalFee", result.getTotalFee());
            orderLogDO.setProcessContent("支付成功：" + JSON.toJSONString(successMap));

            return orderService.insertOrderLog(orderLogDO);
        }
        log.debug("===订单支付成功，没有符合条件的数据要更新===");

        return true;
    }

    // 退款成功
    @Transactional
    @Retry
    public boolean successRefund(WxPayRefundNotifyResult result){

        OrderDO orderDO = orderService.getByOrderCode(result.getReqInfo().getOutTradeNo());
        if(ObjectUtils.isEmpty(orderDO)){
            log.error("不存在该订单数据");
            return true;
        }
        Integer orderStatus = orderDO.getOrderStatus();
        Integer version = orderDO.getVersion();
        if(OrderStatusEnum.APPLY_REFUND.getCode().equals(orderStatus)){

            OrderStatusEnum nextStatus = OrderStatusEnum.APPLY_REFUND.getNext(true);

            // 更新订单
            boolean updateOrderStatus = orderService.updateOrderStatus(orderDO.getId(), nextStatus.getCode(), version);
            if(updateOrderStatus){
                log.error("更新订单状态失败");
                throw new RetryException(CodeMsg.RETRY_ON_FAIL);
            }

            // 插入订单流水
            OrderLogDO orderLogDO = buildOrderLogDO(orderDO, result);
            boolean insertOrderLog = orderService.insertOrderLog(orderLogDO);

            // 插入资金日志
            CoinLogDO coinLogDO = buildCoinLogDO(orderDO);
            boolean insertLog = coinService.insertLog(coinLogDO);

            return insertLog && insertOrderLog;
        }

        return true;
    }


    // 退款失败
    @Transactional
    @Retry
    public boolean failRefund(WxPayRefundNotifyResult result){


        OrderDO orderDO = orderService.getByOrderCode(result.getReqInfo().getOutTradeNo());
        if(ObjectUtils.isEmpty(orderDO)){
            log.error("不存在该订单数据");
            return true;
        }
        Integer orderStatus = orderDO.getOrderStatus();
        Integer version = orderDO.getVersion();
        if(OrderStatusEnum.APPLY_REFUND.getCode().equals(orderStatus)){
            // 设置异常单
            boolean update = orderService.updateOrder4FailRefund(orderDO.getId(), version);
            if(update){
                log.error("更新订单状态失败");
                throw new RetryException(CodeMsg.RETRY_ON_FAIL);
            }

            // 插入订单日志
            OrderLogDO orderLogDO = new OrderLogDO();
            orderLogDO.setId(UUIDUtil.getOrderLogCode());
            orderLogDO.setPreStatus(orderDO.getOrderStatus());
            orderLogDO.setProcessType(OrderProcessTypeEnum.FAIL.getCode());
            orderLogDO.setOrderCode(orderDO.getId());
            orderLogDO.setAfterStatus(orderDO.getOrderStatus());
            orderLogDO.setModifier("wxNotify");
            orderLogDO.setCreator("wxNotify");
            HashMap<Object, Object> errorMap = Maps.newHashMap();
            errorMap.put("wxOrderRefundCode", result.getReqInfo().getOutRefundNo());
            errorMap.put("wxRefundId",result.getReqInfo().getRefundId());
            errorMap.put("wxRefundFee",result.getReqInfo().getRefundFee());
            orderLogDO.setProcessContent("退款失败：" + JSON.toJSONString(errorMap));
            return orderService.insertOrderLog(orderLogDO);
        }

        return true;
    }


    private OrderLogDO buildOrderLogDO(OrderDO orderDO, WxPayRefundNotifyResult result) {

        OrderLogDO orderLogDO = new OrderLogDO();
        orderLogDO.setId(UUIDUtil.getOrderLogCode());
        orderLogDO.setPreStatus(orderDO.getOrderStatus());
        orderLogDO.setProcessType(OrderProcessTypeEnum.SUCCESS.getCode());
        orderLogDO.setOrderCode(orderDO.getId());
        orderLogDO.setAfterStatus(OrderStatusEnum.APPLY_REFUND.getNext(true).getCode());
        orderLogDO.setModifier("wxNotify");
        orderLogDO.setCreator("wxNotify");
        HashMap<Object, Object> successMap = Maps.newHashMap();
        successMap.put("wxOrderRefundCode", result.getReqInfo().getOutRefundNo());
        successMap.put("wxRefundId",result.getReqInfo().getRefundId());
        successMap.put("wxRefundFee",result.getReqInfo().getRefundFee());
        orderLogDO.setProcessContent("退款成功：" + JSON.toJSONString(successMap));

        return orderLogDO;
    }


    private CoinLogDO buildCoinLogDO(OrderDO orderDO) {
        CoinLogDO coinLogDO = new CoinLogDO();
        coinLogDO.setId(UUIDUtil.getCoinLogCode());
        coinLogDO.setModifier("wxNotify");
        coinLogDO.setCreator("wxNotify");
        coinLogDO.setProcessType(CoinProcessTypeEnum.REFUND.getCode());
        coinLogDO.setMerchantCode(wxMaConfig.getMchid());
        coinLogDO.setSourceId(orderDO.getTutorId());
        coinLogDO.setTargetId(orderDO.getUserId());
        coinLogDO.setCoinAmount(orderDO.getTotalCost());
        coinLogDO.setOrderCode(orderDO.getId());
        return coinLogDO;
    }
}
