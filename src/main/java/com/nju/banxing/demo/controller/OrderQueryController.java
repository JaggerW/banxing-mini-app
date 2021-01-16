package com.nju.banxing.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.BasePaged;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.enums.CommentStatusEnum;
import com.nju.banxing.demo.enums.OrderStatusEnum;
import com.nju.banxing.demo.enums.TutorStatusEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.request.OrderListQuery;
import com.nju.banxing.demo.service.*;
import com.nju.banxing.demo.util.TXMeetingUtil;
import com.nju.banxing.demo.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: jaggerw
 * @Description: 订单查询
 * @Date: 2020/12/24
 */
@RestController
@RequestMapping("/order/query")
@Slf4j
public class OrderQueryController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private TutorService tutorService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReadService readService;


    // 预约订单列表（导师端）
    @GetMapping("/reserve_list")
    @MethodLog("获取预约申请列表（导师中心）")
    public PagedResult<OrderListInfoVO> getReserveList(String openid,
                                                       @RequestBody OrderListQuery query){
        IPage<Map<String, Object>> orderList = orderService.getOrderListByTutorIdAndProcessFlag(openid, query.getProcessFlag(), query.getPageIndex(), query.getPageSize());
        List<OrderListInfoVO> data = buildOrderVO(orderList);
        readService.updateOrderApplyTimeByTutorId(openid);
        return PagedResult.success(data,orderList.getCurrent(),orderList.getSize(),orderList.getTotal(),orderList.getPages());
    }

    @GetMapping("/reserve_detail")
    @MethodLog("获取预约申请详情（导师中心）")
    public SingleResult<ReserveOrderDetailVO> getReserveDetail(String openid,
                                                               @RequestParam("orderCode") String orderCode){
        Map<String, Object> voMap = orderService.getReserveOrderDetailByOrderCodeAndTutorId(orderCode, openid);
        if(ObjectUtils.isEmpty(voMap)){
            throw new GlobalException(CodeMsg.NULL_ORDER);
        }
        ReserveOrderDetailVO detailVO = buildReserveDetail(voMap);
        return SingleResult.success(detailVO);
    }

    // 评价订单列表（学员端）
    @GetMapping("/comment_list")
    @MethodLog("获取评价订单列表（用户中心）")
    public PagedResult<OrderListInfoVO> getCommentList(String openid,
                                                       @RequestBody OrderListQuery query){
        IPage<Map<String, Object>> orderList = orderService.getCommentListByUserIdAndProcessFlag(openid, query.getProcessFlag(), query.getPageIndex(), query.getPageSize());
        List<OrderListInfoVO> data = buildOrderVO(orderList);
        readService.updateOrderCommentTimeByUserId(openid);
        return PagedResult.success(data,orderList.getCurrent(),orderList.getSize(),orderList.getTotal(),orderList.getPages());
    }

    @GetMapping("/comment_detail")
    @MethodLog("获取评价订单详情（用户中心）")
    public SingleResult<CommentOrderDetailVO> getCommentDetail(String openid,
                                                               @RequestParam("orderCode") String orderCode){
        Map<String, Object> voMap = orderService.getCommentOrderDetailByOrderCodeAndUserId(orderCode, openid);
        if(ObjectUtils.isEmpty(voMap)){
            throw new GlobalException(CodeMsg.NULL_ORDER);
        }
        CommentOrderDetailVO vo = buildCommentDetail(voMap);
        return SingleResult.success(vo);
    }

    // 已申请订单列表（学员端）
    @GetMapping("/order_list")
    @MethodLog("获取订单列表（用户中心）")
    public PagedResult<OrderListInfoVO> getOrderList(String openid,
                                                     @RequestBody OrderListQuery query){
        IPage<Map<String, Object>> orderList = orderService.getOrderListByUserIdAndProcessFlag(openid, query.getProcessFlag(), query.getPageIndex(), query.getPageSize());
        List<OrderListInfoVO> data = buildOrderVO(orderList);
        return PagedResult.success(data,orderList.getCurrent(),orderList.getSize(),orderList.getTotal(),orderList.getPages());
    }

    @GetMapping("/order_detail")
    @MethodLog("获取订单详情（用户中心）")
    public SingleResult<OrderStatusDetailVO> getOrderDetail(@RequestParam("orderCode") String orderCode){

        OrderDO orderDO = orderService.getByOrderCode(orderCode);
        if(ObjectUtils.isEmpty(orderDO)){
            throw new GlobalException(CodeMsg.NULL_ORDER);
        }
        List<Integer> statusList = orderService.getOrderLogStatusByOrderCode(orderCode).stream().distinct().sorted().collect(Collectors.toList());
        OrderStatusDetailVO vo = buildOrderStatusDetailVO(orderDO, statusList);
        return SingleResult.success(vo);

    }


    // 反馈列表（学员端）
    @GetMapping("/reply_list")
    @MethodLog("获取反馈订单列表（用户中心）")
    public PagedResult<OrderListInfoVO> getReplyOrderList(String openid,
                                                          @RequestBody OrderListQuery query){
        IPage<Map<String, Object>> orderList = orderService.getReplyOrderListByUserIdAndProcessFlag(openid, query.getProcessFlag(), query.getPageIndex(), query.getPageSize());
        List<OrderListInfoVO> data = buildOrderVO(orderList);
        readService.updateOrderReplyTimeByUserId(openid);
        return PagedResult.success(data,orderList.getCurrent(),orderList.getSize(),orderList.getTotal(),orderList.getPages());

    }

    @GetMapping("/reply_detail")
    @MethodLog("获取反馈订单详情（用户中心）")
    public SingleResult<ReplyOrderDetailVO> getReplyDetail(String openid,
                                                           @RequestParam("orderCode") String orderCode){
        Map<String, Object> voMap = orderService.getReplyOrderDetailByOrderCodeAndUserId(orderCode, openid);
        if(ObjectUtils.isEmpty(voMap)){
            throw new GlobalException(CodeMsg.NULL_ORDER);
        }
        ReplyOrderDetailVO detailVO = buildReplyDetail(voMap);
        return SingleResult.success(detailVO);
    }

    // 日程安排列表（学员端、导师端）
    @GetMapping("/schedule_list_user")
    @MethodLog("获取日程安排列表（用户中心）")
    public PagedResult<ScheduleListInfoVO> getScheduleListUser(String openid,
                                                               @RequestBody OrderListQuery query){
        IPage<Map<String, Object>> orderList = orderService.getScheduleListByUserId(openid, query.getProcessFlag(), query.getPageIndex(), query.getPageSize());
        List<ScheduleListInfoVO> data = buildScheduleVO(orderList);
        return PagedResult.success(data,orderList.getCurrent(),orderList.getSize(),orderList.getTotal(),orderList.getPages());
    }


    @GetMapping("/schedule_list_tutor")
    @MethodLog("获取日程安排列表（导师中心）")
    public PagedResult<OrderListInfoVO> getScheduleListTutor(String openid,
                                                             @RequestBody OrderListQuery query){
        IPage<Map<String, Object>> orderList = orderService.getScheduleListByTutorId(openid, query.getProcessFlag(), query.getPageIndex(), query.getPageSize());
        List<OrderListInfoVO> data = buildOrderVO(orderList);
        return PagedResult.success(data,orderList.getCurrent(),orderList.getSize(),orderList.getTotal(),orderList.getPages());
    }

    @GetMapping("/schedule_detail_tutor")
    @MethodLog("获取日程安排详情（导师中心）")
    public SingleResult<CommentOrderDetailVO> getScheduleDetailTutor(String openid,
                                                                     @RequestParam("orderCode") String orderCode){
        Map<String, Object> voMap = orderService.getScheduleDetailByOrderCodeAndTutorId(orderCode, openid);
        if(ObjectUtils.isEmpty(voMap)){
            throw new GlobalException(CodeMsg.NULL_ORDER);
        }
        CommentOrderDetailVO vo = buildCommentDetail4Tutor(voMap);
        return SingleResult.success(vo);
    }


    // 私有
    private CommentOrderDetailVO buildCommentDetail(Map<String, Object> voMap) {
        CommentOrderDetailVO vo = new CommentOrderDetailVO();

        String orderCode = (String) voMap.get("orderCode");
        vo.setOrderCode(orderCode);
        Timestamp startTime = (Timestamp) voMap.get("reserveStartTime");
        Timestamp endTime = (Timestamp) voMap.get("reserveEndTime");
        vo.setReserveDate(startTime.toLocalDateTime().toLocalDate());
        vo.setReserveStartTime(startTime.toLocalDateTime().toLocalTime());
        vo.setReserveEndTime(endTime.toLocalDateTime().toLocalTime());

        String tutorId = (String) voMap.get("tutorId");
        Map<String, Object> info = tutorService.getTutorInfoById(tutorId);
        vo.setNickName((String) info.get("nickName"));
        vo.setCurrentUniversity((String) info.get("currentUniversity"));
        vo.setCurrentProfession((String) info.get("currentProfession"));

        Map<String, Object> comment = commentService.getCommentInfoByOrderCode(orderCode);
        vo.setCommentContent((String) comment.get("commentContent"));
        vo.setCommentScore((Float) comment.get("commentScore"));

        return vo;

    }

    private CommentOrderDetailVO buildCommentDetail4Tutor(Map<String, Object> voMap) {
        CommentOrderDetailVO vo = new CommentOrderDetailVO();

        String orderCode = (String) voMap.get("orderCode");
        vo.setOrderCode(orderCode);
        Timestamp startTime = (Timestamp) voMap.get("reserveStartTime");
        Timestamp endTime = (Timestamp) voMap.get("reserveEndTime");
        vo.setReserveDate(startTime.toLocalDateTime().toLocalDate());
        vo.setReserveStartTime(startTime.toLocalDateTime().toLocalTime());
        vo.setReserveEndTime(endTime.toLocalDateTime().toLocalTime());

        String userId = (String) voMap.get("userId");
        vo.setNickName(userService.getNickNameById(userId));

        Map<String, Object> comment = commentService.getCommentInfoByOrderCode(orderCode);
        vo.setCommentContent((String) comment.get("commentContent"));
        vo.setCommentScore((Float) comment.get("commentScore"));

        return vo;

    }

    private List<ScheduleListInfoVO> buildScheduleVO(IPage<Map<String, Object>> orderList) {
        return orderList.getRecords().stream().map(map -> {
            ScheduleListInfoVO vo = new ScheduleListInfoVO();
            vo.setOrderCode((String) map.get("orderCode"));

            Timestamp startTime = (Timestamp) map.get("reserveStartTime");
            Timestamp endTime = (Timestamp) map.get("reserveEndTime");
            vo.setReserveDate(startTime.toLocalDateTime().toLocalDate());
            vo.setReserveStartTime(startTime.toLocalDateTime().toLocalTime());
            vo.setReserveEndTime(endTime.toLocalDateTime().toLocalTime());

            vo.setNickName((String) map.get("userName"));
            vo.setConferenceLink((String) map.get("conferenceLink"));
            vo.setConferenceUrl((String) map.get("conferenceUrl"));
            return vo;
        }).collect(Collectors.toList());
    }

    private OrderStatusDetailVO buildOrderStatusDetailVO(OrderDO orderDO, List<Integer> statusList) {
        OrderStatusDetailVO vo = new OrderStatusDetailVO();
        Integer orderStatus = orderDO.getOrderStatus();
        Integer tutorStatus = orderDO.getTutorStatus();
        Integer commentStatus = orderDO.getCommentStatus();
        boolean processFlag = OrderStatusEnum.ORDER_CLOSED.getCode() > orderStatus;
        vo.setOrderStatus(orderStatus);
        vo.setNickName(userService.getNickNameById(orderDO.getTutorId()));
        vo.setOrderCode(orderDO.getId());
        vo.setReserveStartTime(orderDO.getReserveStartTime().toLocalTime());
        vo.setReserveEndTime(orderDO.getReserveEndTime().toLocalTime());
        vo.setReserveDate(orderDO.getReserveStartTime().toLocalDate());
        vo.setProcessFlag(processFlag);

        ArrayList<String> list = Lists.newArrayList();
        for(Integer status : statusList){
            if(OrderStatusEnum.ORDER_PAID.getCode().equals(status)){
                list.add("您已支付费用，预约成功");
            }
            else if (OrderStatusEnum.ORDER_PROCESSING.getCode().equals(status)){
                list.add("导师已确认并下发腾讯会议链接");
            }
            else if (OrderStatusEnum.APPLY_REFUND.getCode().equals(status)){
                if(TutorStatusEnum.REFUSED.getCode().equals(tutorStatus)){
                    list.add("导师已拒绝");
                }else {
                    list.add("导师超时未处理");
                }
            }
            else if (OrderStatusEnum.ORDER_CLOSED.getCode().equals(status)){
                if(CommentStatusEnum.COMMENTED.getCode().equals(commentStatus)){
                    list.add("会议结束，学员评价成功");
                }else {
                    list.add("会议结束，学员超时未评价");
                }
            }
            else if (OrderStatusEnum.ORDER_REFUNDED.getCode().equals(status)){
                list.add("支付费用已按原路径退回");
            }
        }

        if(processFlag){
            if(OrderStatusEnum.ORDER_PAID.getCode().equals(orderStatus)){
                list.add("等待导师确认");
            }
            else if (OrderStatusEnum.ORDER_PROCESSING.getCode().equals(orderStatus) ||
                    OrderStatusEnum.ORDER_COMPLETE.getCode().equals(orderStatus)){
                list.add("等待会议结束，学员进行评价");
            }
            else if (OrderStatusEnum.APPLY_REFUND.getCode().equals(orderStatus)){
                list.add("等待支付费用退回至账户");
            }
        }

        vo.setOrderStatusDetail(list);
        return vo;
    }

    private List<OrderListInfoVO> buildOrderVO(IPage<Map<String, Object>> orderList) {
        return orderList.getRecords().stream().map(map -> {
            OrderListInfoVO vo = new OrderListInfoVO();
            vo.setOrderCode((String) map.get("orderCode"));

            Timestamp startTime = (Timestamp) map.get("reserveStartTime");
            Timestamp endTime = (Timestamp) map.get("reserveEndTime");
            vo.setReserveDate(startTime.toLocalDateTime().toLocalDate());
            vo.setReserveStartTime(startTime.toLocalDateTime().toLocalTime());
            vo.setReserveEndTime(endTime.toLocalDateTime().toLocalTime());

            vo.setNickName((String) map.get("userName"));
            return vo;
        }).collect(Collectors.toList());
    }

    private ReserveOrderDetailVO buildReserveDetail(Map<String, Object> voMap){
        ReserveOrderDetailVO vo = new ReserveOrderDetailVO();
        vo.setConferenceLink((String) voMap.get("conferenceLink"));
        vo.setConsultationContent((String) voMap.get("consultationContent"));
        vo.setRejectReason((String) voMap.get("rejectReason"));
        vo.setResumeUrl((String) voMap.get("resumeUrl"));
        String userId = (String) voMap.get("userId");
        vo.setNickName(userService.getNickNameById(userId));
        vo.setOrderCode((String) voMap.get("orderCode"));
        vo.setTutorStatus((Integer) voMap.get("tutorStatus"));

        Timestamp startTime = (Timestamp) voMap.get("reserveStartTime");
        Timestamp endTime = (Timestamp) voMap.get("reserveEndTime");
        vo.setReserveDate(startTime.toLocalDateTime().toLocalDate());
        vo.setReserveStartTime(startTime.toLocalDateTime().toLocalTime());
        vo.setReserveEndTime(endTime.toLocalDateTime().toLocalTime());
        return vo;
    }

    private ReplyOrderDetailVO buildReplyDetail(Map<String, Object> voMap) {
        ReplyOrderDetailVO vo = new ReplyOrderDetailVO();
        vo.setOrderCode((String) voMap.get("orderCode"));
        vo.setConferenceLink((String) voMap.get("conferenceLink"));
        vo.setRejectReason((String) voMap.get("rejectReason"));

        String tutorId = (String) voMap.get("tutorId");
        Map<String, Object> info = tutorService.getTutorInfoById(tutorId);
        vo.setNickName((String) info.get("nickName"));
        vo.setCurrentUniversity((String) info.get("currentUniversity"));
        vo.setCurrentProfession((String) info.get("currentProfession"));

        Timestamp startTime = (Timestamp) voMap.get("reserveStartTime");
        Timestamp endTime = (Timestamp) voMap.get("reserveEndTime");
        vo.setReserveDate(startTime.toLocalDateTime().toLocalDate());
        vo.setReserveStartTime(startTime.toLocalDateTime().toLocalTime());
        vo.setReserveEndTime(endTime.toLocalDateTime().toLocalTime());
        return vo;
    }

}
