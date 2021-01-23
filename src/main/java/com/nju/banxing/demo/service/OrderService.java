package com.nju.banxing.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.domain.OrderLogDO;
import com.nju.banxing.demo.domain.mapper.OrderLogMapper;
import com.nju.banxing.demo.domain.mapper.OrderMapper;
import com.nju.banxing.demo.enums.*;
import com.nju.banxing.demo.request.OrderCreateRequest;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private UserService userService;

    // task

    public List<String> getErrorOrderCode(){
        return orderMapper.getErrorOrderCode();
    }

    public List<Object> getAutoRejectOrderCode(){
        return orderMapper.selectObjs(
                new QueryWrapper<OrderDO>().lambda()
                        .select(OrderDO::getId)
                        .eq(OrderDO::getOrderStatus, OrderStatusEnum.ORDER_PAID.getCode())
                        .eq(OrderDO::getRowStatus, RowStatusEnum.VALID.getCode())
                        .le(OrderDO::getReserveStartTime, DateUtil.now().plusHours(3L)));
    }

    public List<Map<String, Object>> getAutoCommentOrderInfo(){

        return orderMapper.getAutoCommentOrderInfo();
    }

    public boolean enableComment(){
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .set(OrderDO::getCommentStatus, CommentStatusEnum.TO_COMMENT.getCode())
                        .eq(OrderDO::getCommentStatus, CommentStatusEnum.NULL.getCode())
                        .eq(OrderDO::getRowStatus, RowStatusEnum.VALID.getCode())
                        .le(OrderDO::getReserveEndTime, DateUtil.now())) > 0;
    }

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

        LocalDate localDate = DateUtil.toLocalDate(request.getReserveDateTimeStamp());
        LocalTime startTime = request.getReserveStartTime();
        LocalDateTime reserveStartTime = LocalDateTime.of(localDate, startTime);
        orderDO.setReserveStartTime(reserveStartTime);
        orderDO.setReserveEndTime(reserveStartTime.plusMinutes(10 * request.getConsultationTimeCount()));

        orderDO.setTotalCost(totalCost);
        orderDO.setTutorId(request.getTutorId());
        orderDO.setUserId(openid);
        orderDO.setVersion(1);
        orderDO.setRowStatus(RowStatusEnum.INVALID.getCode());

        // 订单信息落库
        int orderInsert = orderMapper.insert(orderDO);

        OrderLogDO orderLogDO = new OrderLogDO();
        orderLogDO.setId(UUIDUtil.getOrderLogCode());
        orderLogDO.setCreator(openid);
        orderLogDO.setModifier(openid);
        orderLogDO.setAfterStatus(OrderStatusEnum.ORDER_TO_PAY.getCode());
        orderLogDO.setRowStatus(RowStatusEnum.INVALID.getCode());
        orderLogDO.setOrderCode(orderCode);
        orderLogDO.setProcessType(OrderProcessTypeEnum.SUCCESS.getCode());
        orderLogDO.setProcessContent("订单建立");

        log.debug(orderDO.toString());

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
        Long count = orderMapper.getOrderCountByTutorIdAndProcessFlag(tutorId, processFlag, OrderStatusEnum.ORDER_PAID.getCode(), RowStatusEnum.VALID.getCode());
        List<Map<String, Object>> orderList = orderMapper.getOrderListByTutorIdAndProcessFlag(tutorId, processFlag, OrderStatusEnum.ORDER_PAID.getCode(), RowStatusEnum.VALID.getCode(), offset, pageSize);
        return buildIPage(pageIndex,pageSize,count,orderList);
    }

    public IPage<Map<String, Object>> getOrderListByUserIdAndProcessFlag(String userId, Boolean processFlag, Long pageIndex, Long pageSize) {
        Long offset = (pageIndex - 1) * pageSize;
        Long count = orderMapper.getOrderCountByUserIdAndProcessFlag(userId, processFlag, OrderStatusEnum.ORDER_CLOSED.getCode(), RowStatusEnum.VALID.getCode());
        List<Map<String, Object>> orderList = orderMapper.getOrderListByUserIdAndProcessFlag(userId, processFlag, OrderStatusEnum.ORDER_CLOSED.getCode(), RowStatusEnum.VALID.getCode(), offset, pageSize);
        return buildIPage(pageIndex,pageSize,count,orderList);
    }

    public IPage<Map<String, Object>> getCommentListByUserIdAndProcessFlag(String userId, Boolean processFlag, Long pageIndex, Long pageSize) {
        Long offset = (pageIndex - 1) * pageSize;
        Long count = orderMapper.getCommentOrderCountByUserIdAndProcessFlag(userId, processFlag, CommentStatusEnum.TO_COMMENT.getCode(), RowStatusEnum.VALID.getCode());
        List<Map<String, Object>> orderList = orderMapper.getCommentOrderListByUserIdAndProcessFlag(userId, processFlag, CommentStatusEnum.TO_COMMENT.getCode(), RowStatusEnum.VALID.getCode(), offset, pageSize);
        return buildIPage(pageIndex,pageSize,count,orderList);
    }

    public IPage<Map<String, Object>> getReplyOrderListByUserIdAndProcessFlag(String userId, Boolean processFlag, Long pageIndex, Long pageSize) {
        Long offset = (pageIndex - 1) * pageSize;
        Long count = orderMapper.getReplyOrderCountByUserIdAndProcessFlag(userId, processFlag, TutorStatusEnum.ACCEPTED.getCode(), RowStatusEnum.VALID.getCode());
        List<Map<String, Object>> orderList = orderMapper.getReplyOrderListByUserIdAndProcessFlag(userId, processFlag, TutorStatusEnum.ACCEPTED.getCode(), RowStatusEnum.VALID.getCode(), offset, pageSize);
        return buildIPage(pageIndex,pageSize,count,orderList);
    }

    public IPage<Map<String, Object>> getScheduleListByUserId(String userId, Boolean processFlag, Long pageIndex, Long pageSize) {
        Long offset = (pageIndex - 1) * pageSize;
        Long count = orderMapper.getScheduleCountByUserId(userId, processFlag, OrderStatusEnum.ORDER_CLOSED.getCode(), TutorStatusEnum.ACCEPTED.getCode(), RowStatusEnum.VALID.getCode());
        List<Map<String, Object>> orderList = orderMapper.getScheduleListByUserId(userId, processFlag, OrderStatusEnum.ORDER_CLOSED.getCode(), TutorStatusEnum.ACCEPTED.getCode(), RowStatusEnum.VALID.getCode(), offset, pageSize);
        return buildIPage(pageIndex,pageSize,count,orderList);
    }

    public IPage<Map<String, Object>> getScheduleListByTutorId(String tutorId, Boolean processFlag, Long pageIndex, Long pageSize) {
        Long offset = (pageIndex - 1) * pageSize;
        Long count = orderMapper.getScheduleCountByTutorId(tutorId, processFlag, OrderStatusEnum.ORDER_CLOSED.getCode(), TutorStatusEnum.ACCEPTED.getCode(), RowStatusEnum.VALID.getCode());
        List<Map<String, Object>> orderList = orderMapper.getScheduleListByTutorId(tutorId, processFlag, OrderStatusEnum.ORDER_CLOSED.getCode(), TutorStatusEnum.ACCEPTED.getCode(), RowStatusEnum.VALID.getCode(), offset, pageSize);
        return buildIPage(pageIndex,pageSize,count,orderList);
    }

    private IPage<Map<String, Object>> buildIPage(Long pageIndex, Long pageSize, Long count, List<Map<String, Object>> orderList){
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

    public Map<String, Object> getReserveOrderDetailByOrderCodeAndTutorId(String orderCode, String tutorId){
        return orderMapper.getReserveOrderDetailByOrderCodeAndTutorId(orderCode,tutorId,RowStatusEnum.VALID.getCode());
    }

    public Map<String, Object> getCommentOrderDetailByOrderCodeAndUserId(String orderCode, String userId){
        return orderMapper.getCommentOrderDetailByOrderCodeAndUserId(orderCode,userId,RowStatusEnum.VALID.getCode());
    }

    public Map<String, Object> getScheduleDetailByOrderCodeAndTutorId(String orderCode, String userId){
        return orderMapper.getScheduleDetailByOrderCodeAndTutorId(orderCode,userId,RowStatusEnum.VALID.getCode());
    }

    public Map<String, Object> getReplyOrderDetailByOrderCodeAndUserId(String orderCode, String userId){
        return orderMapper.getReplyOrderDetailByOrderCodeAndUserId(orderCode,userId,RowStatusEnum.VALID.getCode());
    }

    public Map<String, Object> getOrderConferenceInfoByOrderCode(String orderCode){
        return orderMapper.getOrderConferenceInfoByOrderCode(orderCode,RowStatusEnum.VALID.getCode());
    }

    public String getTutorMobileByOrderCode(String orderCode) {
        String tutorId = orderMapper.getTutorIdByCode(orderCode);
        return userService.getMobileById(tutorId);
    }

    public Map<String, Object> getTutorInfoByOrderCode(String orderCode){
        return orderMapper.getTutorInfoByOrderCode(orderCode);
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

    public List<Integer> getOrderLogStatusByOrderCode(String orderCode){
        return orderLogMapper.getOrderStatusByOrderCode(orderCode, RowStatusEnum.VALID.getCode());
    }

    public boolean updateOrder4Accept(String orderCode, Integer orderStatus, Integer version, String content, String meetingUrl){
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getReplyTime, DateUtil.now())
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getTutorStatus, TutorStatusEnum.ACCEPTED.getCode())
                        .set(OrderDO::getVersion, version + 1)
                        .set(OrderDO::getConferenceUrl,meetingUrl)
                        .set(OrderDO::getConferenceLink, content)) > 0;
    }

    public boolean updateOrder4Reject(String orderCode, Integer orderStatus, Integer version, String content){
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getReplyTime, DateUtil.now())
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getTutorStatus, TutorStatusEnum.REFUSED.getCode())
                        .set(OrderDO::getVersion, version + 1)
                        .set(OrderDO::getRejectReason, content)) > 0;
    }

    public boolean updateOrder4AutoReject(String orderCode, Integer orderStatus, Integer version, String content){
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getReplyTime, DateUtil.now())
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getTutorStatus, TutorStatusEnum.AUTO_REFUSED.getCode())
                        .set(OrderDO::getVersion, version + 1)
                        .set(OrderDO::getRejectReason, content)) > 0;
    }

    public boolean updateOrder4SuccessPay(String orderCode, Integer orderStatus, Integer version) {
        return orderMapper.update(null,
                new UpdateWrapper<OrderDO>().lambda()
                        .eq(OrderDO::getId, orderCode)
                        .eq(OrderDO::getVersion, version)
                        .set(OrderDO::getOrderStatus, orderStatus)
                        .set(OrderDO::getRowStatus,RowStatusEnum.VALID.getCode())
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
