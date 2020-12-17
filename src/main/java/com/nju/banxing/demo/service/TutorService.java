package com.nju.banxing.demo.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nju.banxing.demo.annotation.Retry;
import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.domain.TutorDO;
import com.nju.banxing.demo.domain.mapper.TutorMapper;
import com.nju.banxing.demo.domain.mapper.UserMapper;
import com.nju.banxing.demo.enums.OrderStatusEnum;
import com.nju.banxing.demo.enums.TutorApplyStatusEnum;
import com.nju.banxing.demo.enums.TutorStatusEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.RetryException;
import com.nju.banxing.demo.request.TutorHandleOrderRequest;
import com.nju.banxing.demo.request.TutorReapplyRequest;
import com.nju.banxing.demo.request.TutorRegisterRequest;
import com.nju.banxing.demo.request.TutorUpdateRequest;
import com.nju.banxing.demo.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private OrderService orderService;

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

    @Retry
    public boolean accept(String openid, TutorHandleOrderRequest request) {

        OrderDO orderDO = orderService.getByOrderCodeAndTutorId(request.getOrderCode(), openid);
        // 已付款状态
        if(ObjectUtils.isNotEmpty(orderDO) && OrderStatusEnum.ORDER_PAID.getCode().equals(orderDO.getOrderStatus())){
            Integer version = orderDO.getVersion();
            OrderStatusEnum nextStatus = OrderStatusEnum.ORDER_PAID.getNext(true);
            boolean accept = orderService.updateOrder4Accept(orderDO.getId(), nextStatus.getCode(), version, request.getContent());
            if(!accept){
                throw new RetryException(CodeMsg.RETRY_ON_FAIL);
            }
            return true;
        }
        return false;
    }
}
