package com.nju.banxing.demo.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.annotation.Retry;
import com.nju.banxing.demo.config.WxMaConfig;
import com.nju.banxing.demo.domain.CoinDO;
import com.nju.banxing.demo.domain.CoinLogDO;
import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.domain.OrderLogDO;
import com.nju.banxing.demo.domain.mapper.OrderLogMapper;
import com.nju.banxing.demo.domain.mapper.OrderMapper;
import com.nju.banxing.demo.enums.*;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.exception.RetryException;
import com.nju.banxing.demo.request.OrderCreateRequest;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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

    public boolean insertOrderLog(OrderLogDO orderLogDO){
        return orderLogMapper.insert(orderLogDO) > 0;
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

    public Map<String, Object> getOrderDetailByOrderCodeAndTutorId(String orderCode, String tutorId){
        return orderMapper.getOrderDetailByOrderCodeAndTutorId(orderCode,tutorId);
    }

    public Map<String, Object> getOrderConferenceInfoByOrderCode(String orderCode){
        return orderMapper.getOrderConferenceInfoByOrderCode(orderCode);
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

    public String getUserIdByOrderCode(String orderCode){
        return orderMapper.getUserIdByOrderCode(orderCode);
    }

    public BigDecimal getTotalCostByCode(String orderCode) {
        return orderMapper.getTotalCostByCode(orderCode);
    }

    public boolean updateOrderStatus(String orderCode, Integer orderStatus, Integer version) {
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getVersion, version + 1)) > 0;
    }

    public OrderDO getByOrderCodeAndTutorId(String orderCode, String tutorId){
        return orderMapper.selectOne(
                new QueryWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId,orderCode)
                        .eq(OrderDO::getTutorId,tutorId));
    }

    public OrderDO getByOrderCode(String orderCode){
        return orderMapper.selectOne(
                new QueryWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId,orderCode));
    }

    public boolean updateOrder4Accept(String orderCode, Integer orderStatus, Integer version, String content){
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getTutorStatus, TutorStatusEnum.ACCEPTED.getCode())
                        .set(OrderDO::getVersion, version + 1)
                        .set(OrderDO::getConferenceLink, content)) > 0;
    }

    public boolean updateOrder4Reject(String orderCode, Integer orderStatus, Integer version, String content){
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getTutorStatus, TutorStatusEnum.REFUSED.getCode())
                        .set(OrderDO::getVersion, version + 1)
                        .set(OrderDO::getRejectReason, content)) > 0;
    }

    public boolean updateOrder4SuccessPay(String orderCode, Integer orderStatus, Integer version) {
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getTutorStatus, TutorStatusEnum.TO_CONFIRM.getCode())
                        .set(OrderDO::getVersion, version + 1)) > 0;
    }

    public boolean updateOrder4FailRefund(String orderCode, Integer version) {
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getErrorFlag, OrderErrorFlag.UN_NORMAL.getCode())
                        .set(OrderDO::getVersion, version + 1)) > 0;
    }
}
