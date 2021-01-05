package com.nju.banxing.demo.controller;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.domain.CommentDO;
import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.request.OrderCreateRequest;
import com.nju.banxing.demo.request.WxRefundRequest;
import com.nju.banxing.demo.service.CommentService;
import com.nju.banxing.demo.service.OrderService;
import com.nju.banxing.demo.service.TutorService;
import com.nju.banxing.demo.service.WeixinService;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.MathUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.vo.WxRefundVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: jaggerw
 * @Description: 定时任务
 * @Date: 2020/12/24
 */
@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class ScheduleController {


    @Autowired
    private OrderService orderService;

    @Autowired
    private TutorService tutorService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private WeixinService weixinService;


    // 1. 异常单 -> 报警  (30min)
    @Scheduled(cron = "0 0/30 * * * ?")
    @Async
    @MethodLog("定时任务：检测异常单报警，30min")
    public void errorTask(){
        List<String> list = orderService.getErrorOrderCode();
        // TODO 通知管理员异常单

    }

    // 2. 会议结束单 -> 可评论  (1min)
    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    @MethodLog("定时任务：更新会议结束单为可评论状态，1min")
    public void enableComment(){
        boolean b = orderService.enableComment();
        if (b){
            log.info("更新成功");
        }
    }

    // 3. 超时单 -> 导师超时未处理：自动拒绝  (1min)
    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    @MethodLog("定时任务：导师超时未处理自动拒绝，1min")
    public void autoReject(){
        String content = "导师超时未处理，系统已自动拒绝该申请。";
        List<Object> list = orderService.getAutoRejectOrderCode();
        for (Object o : list){
            String orderCode = (String) o;
            // 申请退款+拒绝+发送通知
            try {
                WxRefundRequest refundRequest = buildRefundRequest(orderCode);
                WxRefundVO wxRefundVO = weixinService.applyRefund(refundRequest);
                boolean b = tutorService.autoReject(orderCode, content, wxRefundVO);
                if (b){
                    String userId = orderService.getUserIdByOrderCode(orderCode);
                    sendRejectWxMes(userId, content);
                }
            } catch (WxPayException e) {
                e.printStackTrace();
                log.error("申请微信支付退款失败");
            }
        }
    }


    // 学员超时未评论：自动评论  (1min)
    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    @MethodLog("定时任务：学员超时未评论自动好评，1min")
    public void autoComment(){
        List<Map<String, Object>> list = orderService.getAutoCommentOrderInfo();
        for (Map<String, Object> map : list){
            try {
                String orderCode = (String) map.get("orderCode");
                String userId = (String) map.get("userId");
                String tutorId = (String) map.get("tutorId");
                Integer consultationType = (Integer) map.get("consultationType");
                commentService.publishNewComment(buildCommentDO(orderCode,userId,tutorId,consultationType));
            }catch (Exception e){
                log.error("自动评论定时任务异常：",e);
                log.error("orderCode : {};", map.get("orderCode"));
            }
        }
    }


    private WxRefundRequest buildRefundRequest(String orderCode) {
        BigDecimal totalCost = orderService.getTotalCostByCode(orderCode);
        WxRefundRequest refundRequest = new WxRefundRequest();
        refundRequest.setNonceStr(UUIDUtil.getNonceStr());
        refundRequest.setOrderCode(orderCode);
        refundRequest.setOrderRefundCode(UUIDUtil.getOrderRefundCode());
        refundRequest.setRefundDesc("预约已被取消，将订单已付款退回");
        int fee = MathUtil.bigYuan2Fee(totalCost);
        refundRequest.setTotalFee(fee);
        refundRequest.setRefundFee(fee);
        refundRequest.setNotifyUrl(AppContantConfig.SERVER_PATH_PREFIX + "/pay/refund_notify");
        return refundRequest;
    }

    private void sendRejectWxMes(String openid, String reason){
        List<WxMaSubscribeMessage.Data> dataList = weixinService.getMeetingRejectWxMessage(reason);
        weixinService.sendWxMessage(openid,AppContantConfig.WX_MSG_MEETING_REJECT_TEMPLATE_ID,AppContantConfig.WX_MSG_MEETING_REJECT_PAGE,dataList);
    }

    private CommentDO buildCommentDO(String orderCode, String userId, String tutorId, Integer consultationType){
        CommentDO commentDO = new CommentDO();
        commentDO.setUserId(userId);
        commentDO.setTutorId(tutorId);
        commentDO.setConsultationType(consultationType);
        commentDO.setId(UUIDUtil.getCommentCode());
        commentDO.setOrderCode(orderCode);
        commentDO.setModifier("sys");
        commentDO.setCreator("sys");
        commentDO.setCommentScore(5F);
        commentDO.setCommentContent("用户超时未评论，系统默认好评。");
        return commentDO;
    }


}
