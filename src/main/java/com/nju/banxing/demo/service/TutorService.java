package com.nju.banxing.demo.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.annotation.Retry;
import com.nju.banxing.demo.config.WxMaConfig;
import com.nju.banxing.demo.domain.*;
import com.nju.banxing.demo.domain.mapper.OrderLogMapper;
import com.nju.banxing.demo.domain.mapper.TutorMapper;
import com.nju.banxing.demo.domain.mapper.UserMapper;
import com.nju.banxing.demo.enums.*;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.RetryException;
import com.nju.banxing.demo.request.TutorHandleOrderRequest;
import com.nju.banxing.demo.request.TutorReapplyRequest;
import com.nju.banxing.demo.request.TutorRegisterRequest;
import com.nju.banxing.demo.request.TutorUpdateRequest;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.vo.WxRefundVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 导师表 服务类
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-11
 */
@Service
@Slf4j
public class TutorService {

    @Autowired
    private TutorMapper tutorMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CoinService coinService;

    @Autowired
    private WxMaConfig wxMaConfig;

    public int getStatus(String tutorId){
        Integer statusById = tutorMapper.getStatusById(tutorId);
        if(null == statusById){
            statusById = -1;
        }
        return statusById;
    }

    public Map<String, Object> getApplyInfoById(String tutorId){
        return tutorMapper.getApplyInfoById(tutorId);
    }

    /**
     * 申请导师
     *
     * @param openid
     * @param request
     * @return
     */
    public boolean register(String openid, TutorRegisterRequest request) {
        TutorDO tutorDO = new TutorDO();
        BeanUtils.copyProperties(request, tutorDO);
        tutorDO.setId(openid);
        tutorDO.setWorkTime(JSON.toJSONString(request.getWorkTimeList()));
        tutorDO.setKeyword(request.getCurrentUniversity() + request.getCurrentProfession());
        tutorDO.setCreator(openid);
        tutorDO.setModifier(openid);
        tutorDO.setStatus(TutorApplyStatusEnum.TO_VERIFY.getCode());
        String nickName = userMapper.getNickNameById(openid);
        tutorDO.setNickName(nickName);

        return tutorMapper.insert(tutorDO) > 0;
    }

    /**
     * 重新提交审核信息
     * @param openid
     * @param request
     * @return
     */
    public boolean reapply(String openid, TutorReapplyRequest request){
        int update = tutorMapper.update(null,
                new UpdateWrapper<TutorDO>().lambda()
                        .eq(TutorDO::getId, openid)
                        .set(TutorDO::getCurrentUniversity, request.getCurrentUniversity())
                        .set(TutorDO::getCurrentProfession, request.getCurrentProfession())
                        .set(TutorDO::getKeyword,request.getCurrentUniversity()+request.getCurrentProfession())
                        .set(TutorDO::getStudentCardHome, request.getStudentCardHome())
                        .set(TutorDO::getStudentCardInfo, request.getStudentCardInfo())
                        .set(TutorDO::getStudentCardRegister, request.getStudentCardRegister())
                        .set(TutorDO::getStatus,TutorApplyStatusEnum.TO_VERIFY.getCode())
                        .set(TutorDO::getModifyTime, DateUtil.now())
                        .set(TutorDO::getModifier, openid));
        return update>0;
    }

    /**
     * 更新导师基本信息
     * @param openid
     * @param request
     * @return
     */
    public boolean update(String openid, TutorUpdateRequest request){
        TutorDO tutorDO = new TutorDO();
        BeanUtils.copyProperties(request, tutorDO);
        tutorDO.setId(openid);
        tutorDO.setWorkTime(JSON.toJSONString(request.getWorkTimeList()));
        tutorDO.setModifier(openid);
        return tutorMapper.updateById(tutorDO) > 0;

    }

    /**
     * 获取审核状态
     *
     * @param openid
     * @return
     */
    public int getStatusById(String openid) {
        List<Map<String, Object>> maps = tutorMapper.selectMaps(
                new QueryWrapper<TutorDO>().lambda()
                        .select(TutorDO::getStatus)
                        .eq(TutorDO::getId, openid));
        return (int) maps.get(0).get("status");
    }

    /**
     * 获取工作时间列表
     * @param openid
     * @return
     */
    public String getWorkTimeById(String openid){
        return tutorMapper.getWorkTimeById(openid);
    }


    /**
     * 查询
     * @param type
     * @param keyword
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public IPage<TutorDO> getAll(Integer type, String keyword, Long pageIndex, Long pageSize) {

        Page<TutorDO> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<TutorDO> queryWrapper = new QueryWrapper<TutorDO>().lambda()
                .eq(TutorDO::getStatus, TutorApplyStatusEnum.VERIFY_PASS.getCode())
                .eq(TutorDO::getConsultationType, type)
                .and(StringUtils.isNotEmpty(keyword),
                        qw -> qw.like(TutorDO::getNickName, keyword).or()
                                .like(TutorDO::getKeyword, keyword).or()
                                .like(TutorDO::getCurrentUniversity, keyword).or()
                                .like(TutorDO::getCurrentProfession, keyword));
        return tutorMapper.selectPage(page, queryWrapper);
    }

    public TutorDO getById(String tutorId){
        return tutorMapper.selectById(tutorId);
    }

    public BigDecimal getConsultationCost(String tutorId){
        return tutorMapper.getConsultationCost(tutorId);
    }

    @Transactional
    @Retry
    public boolean accept(String tutorId, TutorHandleOrderRequest request, String meetingUrl) {

        OrderDO orderDO = orderService.getByOrderCodeAndTutorId(request.getOrderCode(), tutorId);
        // 已付款状态
        if(ObjectUtils.isNotEmpty(orderDO) && OrderStatusEnum.ORDER_PAID.getCode().equals(orderDO.getOrderStatus())){

            // 更新订单
            Integer version = orderDO.getVersion();
            OrderStatusEnum nextStatus = OrderStatusEnum.ORDER_PAID.getNext(true);
            boolean accept = orderService.updateOrder4Accept(orderDO.getId(), nextStatus.getCode(), version, request.getContent(), meetingUrl);
            if(!accept){
                throw new RetryException(CodeMsg.RETRY_ON_FAIL);
            }

            // 转入待提资金
            //更新用户资金表
            CoinDO coinDO = coinService.selectByOpenid(tutorId);
            if (null == coinDO) {
                coinService.insert(tutorId);
                coinDO = coinService.selectByOpenid(tutorId);
            }
            BigDecimal occupyAmount = coinDO.getOccupyAmount();
            BigDecimal add = occupyAmount.add(orderDO.getTotalCost());
            coinDO.setOccupyAmount(add);
            coinDO.setModifier(tutorId);
            coinDO.setModifyTime(DateUtil.now());
            boolean updateCoin = coinService.update(coinDO);

            // 插入资金流水
            CoinLogDO coinLogDO = new CoinLogDO();
            coinLogDO.setId(UUIDUtil.getCoinLogCode());
            coinLogDO.setOrderCode(orderDO.getId());
            coinLogDO.setCoinAmount(orderDO.getTotalCost());
            coinLogDO.setSourceId(orderDO.getUserId());
            coinLogDO.setTargetId(tutorId);
            coinLogDO.setMerchantCode(wxMaConfig.getMchid());
            coinLogDO.setProcessType(CoinProcessTypeEnum.PAY.getCode());
            coinLogDO.setCreator(tutorId);
            coinLogDO.setModifier(tutorId);
            boolean insertCoinLog = coinService.insertLog(coinLogDO);

            return updateCoin && insertCoinLog;
        }
        return false;
    }

    @Transactional
    @Retry
    public boolean reject(String tutorId, TutorHandleOrderRequest request, WxRefundVO wxRefundVO) {
        OrderDO orderDO = orderService.getByOrderCodeAndTutorId(request.getOrderCode(), tutorId);
        // 已付款状态
        if(ObjectUtils.isNotEmpty(orderDO) && OrderStatusEnum.ORDER_PAID.getCode().equals(orderDO.getOrderStatus())){

            // 更新订单
            Integer version = orderDO.getVersion();
            OrderStatusEnum nextStatus = OrderStatusEnum.ORDER_PAID.getNext(false);
            boolean reject = orderService.updateOrder4Reject(orderDO.getId(), nextStatus.getCode(), version, request.getContent());
            if(!reject){
                throw new RetryException(CodeMsg.RETRY_ON_FAIL);
            }

            // 更新订单流水+退单单号
            OrderLogDO orderLogDO = new OrderLogDO();
            orderLogDO.setId(UUIDUtil.getOrderLogCode());
            orderLogDO.setCreator(tutorId);
            orderLogDO.setModifier(tutorId);
            orderLogDO.setPreStatus(orderDO.getOrderStatus());
            orderLogDO.setAfterStatus(nextStatus.getCode());
            orderLogDO.setOrderCode(orderDO.getId());
            orderLogDO.setProcessType(OrderProcessTypeEnum.SUCCESS.getCode());
            HashMap<Object, Object> successMap = Maps.newHashMap();
            successMap.put("wxOrderRefundCode", wxRefundVO.getOrderRefundCode());
            successMap.put("wxRefundId",wxRefundVO.getRefundId());
            successMap.put("wxRefundFee",wxRefundVO.getRefundFee());
            orderLogDO.setProcessContent("申请退款：" + JSON.toJSONString(successMap));
            int insertOrderLog = orderLogMapper.insert(orderLogDO);

            return insertOrderLog > 0;

        }
        return false;
    }
}
