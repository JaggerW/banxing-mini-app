package com.nju.banxing.demo.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.config.WxMaConfig;
import com.nju.banxing.demo.domain.CoinDO;
import com.nju.banxing.demo.domain.CoinLogDO;
import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.domain.OrderLogDO;
import com.nju.banxing.demo.domain.mapper.OrderLogMapper;
import com.nju.banxing.demo.domain.mapper.OrderMapper;
import com.nju.banxing.demo.enums.CoinProcessTypeEnum;
import com.nju.banxing.demo.enums.OrderProcessTypeEnum;
import com.nju.banxing.demo.enums.OrderStatusEnum;
import com.nju.banxing.demo.enums.TutorStatusEnum;
import com.nju.banxing.demo.request.OrderCreateRequest;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: jaggerw
 * @Description: 订单
 * @Date: 2020/11/25
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private CoinService coinService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxMaConfig wxMaConfig;

    @Transactional
    public boolean initOrder(String openid, String orderCode, BigDecimal consultationCost, BigDecimal totalCost, OrderCreateRequest request) {
        log.debug("====ready to insert order");

        OrderDO orderDO = new OrderDO();
        orderDO.setConsultationCost(consultationCost);
        orderDO.setConsultationTime(request.getConsultationTimeCount());
        orderDO.setConsultationType(request.getConsultationType());
        orderDO.setConsultationContent(request.getConsultationContent());
        orderDO.setResumeUrl(request.getResumeUrl());
        orderDO.setId(orderCode);
        orderDO.setCreator(openid);
        orderDO.setModifier(openid);
        orderDO.setOrderStatus(OrderStatusEnum.ORDER_TO_PAY.getCode());
        orderDO.setReserveDate(DateUtil.toLocalDate(request.getReserveDateTimeStamp()));
        LocalTime reserveStartTime = request.getReserveStartTime();
        orderDO.setReserveStartTime(reserveStartTime);
        orderDO.setReserveEndTime(reserveStartTime.plusMinutes(10 * request.getConsultationTimeCount()));
        orderDO.setTotalCost(totalCost);
        orderDO.setTutorId(request.getTutorId());
        orderDO.setUserId(openid);
        orderDO.setVersion(1);

        OrderLogDO orderLogDO = new OrderLogDO();
        orderLogDO.setId(UUIDUtil.getOrderLogCode());
        orderLogDO.setCreator(openid);
        orderLogDO.setModifier(openid);
        orderLogDO.setAfterStatus(OrderStatusEnum.ORDER_TO_PAY.getCode());
        orderLogDO.setOrderCode(orderCode);
        orderLogDO.setProcessType(OrderProcessTypeEnum.SUCCESS.getCode());
        orderLogDO.setProcessContent("订单建立");

        log.debug(orderDO.toString());

        // 订单信息落库
        int orderInsert = orderMapper.insert(orderDO);

        // 订单流水落库
        int orderLogInsert = orderLogMapper.insert(orderLogDO);

        log.debug("==== end the insert order");

        return orderInsert > 0 && orderLogInsert > 0;

    }

    @Transactional
    public boolean failPay(WxPayOrderNotifyResult result, Integer orderStatus, Integer version) {
        String orderCode = result.getOutTradeNo();
        String openid = result.getOpenid();
        if (OrderStatusEnum.ORDER_TO_PAY.getCode().equals(orderStatus)) {
            OrderStatusEnum nextOrderStatus = OrderStatusEnum.getEnumByCode(orderStatus).getNext(false);
            int update = updateOrderStatus(orderCode, nextOrderStatus.getCode(), version);

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
            int insert = orderLogMapper.insert(orderLogDO);

            return update > 0 && insert > 0;
        }
        return true;
    }

    @Transactional
    public boolean successPay(WxPayOrderNotifyResult result, Integer orderStatus, Integer version) {

        log.debug("===订单支付成功，开始更新数据===");

        String orderCode = result.getOutTradeNo();
        String openid = result.getOpenid();
        if (OrderStatusEnum.ORDER_TO_PAY.getCode().equals(orderStatus) ||
                OrderStatusEnum.ORDER_FAIL_PAY.getCode().equals(orderStatus)) {

            // 获取订单数据
            OrderDO orderDO = orderMapper.selectById(orderCode);
            String tutorId = orderDO.getTutorId();

            // 更新订单状态
            OrderStatusEnum nextOrderStatus = OrderStatusEnum.getEnumByCode(orderStatus).getNext(true);
            int updateOrder = updateOrder4SuccessPay(orderCode, nextOrderStatus.getCode(), version);

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
            int insertOrderLog = orderLogMapper.insert(orderLogDO);

            // TODO 将资金表相关移至导师处理处
            // 更新用户资金表
            CoinDO coinDO = coinService.selectByOpenid(tutorId);
            if (null == coinDO) {
                coinService.insert(tutorId);
                coinDO = coinService.selectByOpenid(tutorId);
            }
            BigDecimal occupyAmount = coinDO.getOccupyAmount();
            BigDecimal add = occupyAmount.add(orderDO.getTotalCost());
            coinDO.setOccupyAmount(add);
            coinDO.setModifier(openid);
            coinDO.setModifyTime(DateUtil.now());
            int updateCoin = coinService.update(coinDO);

            // 插入资金流水
            CoinLogDO coinLogDO = new CoinLogDO();
            coinLogDO.setId(UUIDUtil.getCoinLogCode());
            coinLogDO.setTradeCode(result.getTransactionId());
            coinLogDO.setOrderCode(result.getOutTradeNo());
            coinLogDO.setCoinAmount(orderDO.getTotalCost());
            coinLogDO.setSourceId(openid);
            coinLogDO.setTargetId(tutorId);
            coinLogDO.setMerchantCode(wxMaConfig.getMchid());
            coinLogDO.setProcessType(CoinProcessTypeEnum.PAY.getCode());
            coinLogDO.setCreator(openid);
            coinLogDO.setModifier(openid);
            int insertCoinLog = coinService.insertLog(coinLogDO);

            log.debug("===订单支付成功，更新成功===");

            return updateOrder > 0 && insertOrderLog > 0 && updateCoin > 0 && insertCoinLog > 0;
        }
        log.debug("===订单支付成功，没有符合条件的数据要更新===");

        return true;
    }

    public IPage<Map<String, Object>> getOrderListByTutorIdAndProcessFlag(String tutorId, Boolean processFlag, Long pageIndex, Long pageSize) {
        Long offset = (pageIndex - 1) * pageSize;
        Long count = orderMapper.getOrderCountByTutorIdAndProcessFlag(tutorId, processFlag, OrderStatusEnum.ORDER_PAID.getCode());
        List<Map<String, Object>> orderList = orderMapper.getOrderListByTutorIdAndProcessFlag(tutorId, processFlag, OrderStatusEnum.ORDER_PAID.getCode(), offset, pageSize);
        Page<Map<String, Object>> mapPage = new Page<>();
        mapPage.setRecords(orderList);
        if(count == 0L){
            mapPage.setPages(0);
        }else {
            mapPage.setPages(count % pageSize == 0 ? count / pageSize : count / pageIndex + 1);
        }
        mapPage.setSize(pageSize);
        mapPage.setCurrent(pageIndex);
        mapPage.setTotal(count);
        return mapPage;
    }

    public String getTutorMobileByOrderCode(String orderCode) {
        String tutorId = orderMapper.getTutorIdByCode(orderCode);
        return userService.getMobileById(tutorId);
    }

    public Integer getStatusByCode(String orderCode) {
        return orderMapper.getStatusByCode(orderCode);
    }

    public Map<String, Integer> getStatusAndVersionByCode(String orderCode) {
        return orderMapper.getStatusAndVersionByCode(orderCode);
    }

    public BigDecimal getTotalCostByCode(String orderCode) {
        return orderMapper.getTotalCostByCode(orderCode);
    }

    public int updateOrderStatus(String orderCode, Integer orderStatus, Integer version) {
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getVersion, version + 1));
    }

    private int updateOrder4SuccessPay(String orderCode, Integer orderStatus, Integer version) {
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getTutorStatus, TutorStatusEnum.TO_CONFIRM.getCode())
                        .set(OrderDO::getVersion, version + 1));
    }
}
