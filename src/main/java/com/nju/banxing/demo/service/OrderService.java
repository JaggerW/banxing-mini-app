package com.nju.banxing.demo.service;

import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.domain.OrderLogDO;
import com.nju.banxing.demo.domain.mapper.OrderLogMapper;
import com.nju.banxing.demo.domain.mapper.OrderMapper;
import com.nju.banxing.demo.request.OrderCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public boolean initOrder(String openid, OrderCreateRequest request){
        OrderDO orderDO = new OrderDO();

        OrderLogDO orderLogDO = new OrderLogDO();

        // 订单信息落库
        int insert = orderMapper.insert(orderDO);

        // 订单流水落库
        int insert1 = orderLogMapper.insert(orderLogDO);

        return insert>0&&insert1>0;

    }

    public void failPay(){

    }

    public void successPay(){

    }

    public Integer getStatusByIdAndCode(String openid, String orderCode){
        return orderMapper.getStatusByIdAndCode(openid,orderCode);
    }
}
