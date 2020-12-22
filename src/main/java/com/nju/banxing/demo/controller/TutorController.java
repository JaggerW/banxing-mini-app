package com.nju.banxing.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.TimePair;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.enums.ConsultationTypeEnum;
import com.nju.banxing.demo.enums.OrderStatusEnum;
import com.nju.banxing.demo.enums.TutorStatusEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.request.*;
import com.nju.banxing.demo.service.*;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.MathUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.util.WxMessageUtil;
import com.nju.banxing.demo.vo.ReserveOrderDetailVO;
import com.nju.banxing.demo.vo.ReserveOrderInfoVO;
import com.nju.banxing.demo.vo.WxMessageVO;
import com.nju.banxing.demo.vo.WxRefundVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: jaggerw
 * @Description: 导师
 * @Date: 2020/11/12
 */
@RestController
@RequestMapping("/tutor")
@Slf4j
public class TutorController {


    @Autowired
    private TutorService tutorService;

    @Autowired
    private AliyunService aliyunService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private WeixinService weixinService;

    @PostMapping("/handle_order")
    @MethodLog("导师提交预约申请处理结果")
    public SingleResult<String> handleOrder(String openid,
                                            @Validated @RequestBody TutorHandleOrderRequest request){
        if(ObjectUtils.isEmpty(request)){
            return SingleResult.error(CodeMsg.BIND_ERROR.fillArgs("request不得为空"));
        }
        if(TutorStatusEnum.ACCEPTED.getCode().equals(request.getHandleType())){
            // 同意
            // 校验参数
            WxMessageVO wxMessageVO = WxMessageUtil.parseMes(request.getContent());
            checkMesVO(wxMessageVO ,request);

            // 更新订单
            boolean accept = tutorService.accept(openid, request, wxMessageVO.getMeetingUrl());
            if(accept){
                // 通知学员
                sendWxMes(request.getOrderCode(),openid);

                // 处理成功
                return SingleResult.success("提交成功，请按照约定的时间完成咨询服务");
            }else {
                throw new GlobalException(CodeMsg.SERVER_ERROR);
            }

        }
        else if (TutorStatusEnum.REFUSED.getCode().equals(request.getHandleType())){
            // 拒绝
            // 校验参数
            String content = request.getContent();
            checkRejectContent(content);
            WxRefundRequest refundRequest = buildRefundRequest(openid, request);
            try {
                WxRefundVO wxRefundVO = weixinService.applyRefund(refundRequest);

                boolean reject = tutorService.reject(openid, request, wxRefundVO);
                if(reject){
                    return SingleResult.success("提交成功，系统会将原因告知学员，同时为了更好的提供服务，请您及时更新自己的工作时间信息");
                }else {
                    throw new GlobalException(CodeMsg.SERVER_ERROR);
                }

            } catch (WxPayException e) {
                e.printStackTrace();
                log.error("申请微信支付退款失败");
                throw new GlobalException(CodeMsg.SERVER_ERROR);
            }

        }

        return SingleResult.error(CodeMsg.BIND_ERROR.fillArgs("请选择同意或拒绝并填写相关信息后再点击提交"));

    }

    private WxRefundRequest buildRefundRequest(String tutorId, TutorHandleOrderRequest request) {
        OrderDO orderDO = orderService.getByOrderCodeAndTutorId(request.getOrderCode(), tutorId);
        if (ObjectUtils.isNotEmpty(orderDO) && OrderStatusEnum.ORDER_PAID.getCode().equals(orderDO.getOrderStatus())) {
            // 已付款状态

            WxRefundRequest refundRequest = new WxRefundRequest();
            refundRequest.setNonceStr(UUIDUtil.getNonceStr());
            refundRequest.setOrderCode(orderDO.getId());
            refundRequest.setOrderRefundCode(UUIDUtil.getOrderRefundCode());
            refundRequest.setRefundDesc("预约已被取消，将订单已付款退回");
            int fee = MathUtil.bigYuan2Fee(orderDO.getTotalCost());
            refundRequest.setTotalFee(fee);
            refundRequest.setRefundFee(fee);
            refundRequest.setNotifyUrl(AppContantConfig.SERVER_PATH_PREFIX + "/pay/refund_notify");
            return refundRequest;
        }

        log.error("查无此单或该单状态有误！");
        throw new GlobalException(CodeMsg.SERVER_ERROR);
    }

    private void sendWxMes(String orderCode, String openid) {
        // TODO 发送微信小程序通知
        OrderDO orderDO = orderService.getByOrderCodeAndTutorId(orderCode, openid);
        String userId = orderDO.getUserId();
        Integer consultationType = orderDO.getConsultationType();
        String typeName = ConsultationTypeEnum.getEnumByCode(consultationType).getName();

    }

    private void checkMesVO(WxMessageVO wxMessageVO, TutorHandleOrderRequest request) {

        if(ObjectUtils.isEmpty(wxMessageVO)){
            throw new GlobalException(CodeMsg.ERROR_MEETING_MESSAGE);
        }
        if(StringUtils.isEmpty(wxMessageVO.getMeetingId())){
            throw new GlobalException(CodeMsg.ERROR_MEETING_MESSAGE);
        }
        if(StringUtils.isEmpty(wxMessageVO.getMeetingUrl())){
            throw new GlobalException(CodeMsg.ERROR_MEETING_MESSAGE);
        }
        if(StringUtils.isEmpty(wxMessageVO.getMeetingTime())){
            throw new GlobalException(CodeMsg.ERROR_MEETING_MESSAGE);
        }
        try {
            LocalDateTime meetingStartTime = wxMessageVO.getMeetingStartTime();
            Map<String, Object> map = orderService.getOrderConferenceInfoByOrderCode(request.getOrderCode());

            Timestamp startTime = (Timestamp) map.get("reserveStartTime");
            if (!meetingStartTime.equals(startTime.toLocalDateTime())){
                throw new GlobalException(CodeMsg.ERROR_MEETING_TIME);
            }
        }catch (Exception e){
            throw new GlobalException(CodeMsg.ERROR_MEETING_TIME);
        }
    }

    private void checkRejectContent(String content){
        if(StringUtils.isEmpty(content)){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("请填写拒绝原因后点击提交"));
        }
        if(500 < content.length()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("拒绝原因请不要超过500字"));
        }
    }

    @GetMapping("/reserve_list")
    @MethodLog("获取预约申请列表")
    public PagedResult<ReserveOrderInfoVO> getReserveList(String openid,
                                                          ReserveOrderListQuery query){
        IPage<Map<String, Object>> orderList = orderService.getOrderListByTutorIdAndProcessFlag(openid, query.getProcessFlag(), query.getPageIndex(), query.getPageSize());
        List<ReserveOrderInfoVO> data = buildReserveVO(orderList);
        return PagedResult.success(data,orderList.getCurrent(),orderList.getSize(),orderList.getTotal(),orderList.getPages());
    }

    @GetMapping("/reserve_detail")
    @MethodLog("获取预约申请详情")
    public SingleResult<ReserveOrderDetailVO> getReserveDetail(String openid,
                                                               @RequestParam("orderCode") String orderCode){
        Map<String, Object> voMap = orderService.getOrderDetailByOrderCodeAndTutorId(orderCode, openid);
        ReserveOrderDetailVO detailVO = buildReserveDetail(voMap);
        return SingleResult.success(detailVO);
    }

    @PostMapping("/register")
    @MethodLog("申请注册导师")
    public SingleResult<Boolean> register(String openid,
                                          @Validated @RequestBody TutorRegisterRequest request){
        Integer consultationType = request.getConsultationType();
        if(ConsultationTypeEnum.KAO_YAN.getCode().equals(consultationType)){
            checkParam(request);
        }

        checkWorkTime(request);

        boolean flag = tutorService.register(openid,request);
        if(flag){
            // todo 通知管理员审核


            return SingleResult.success(true);
        }else{
            return SingleResult.error(CodeMsg.TUTOR_FAIL_REGISTER);
        }
    }

    @PostMapping("/reapply")
    @MethodLog("重新提交导师注册申请")
    public SingleResult<Boolean> reapply(String openid,
                                         @Validated @RequestBody TutorReapplyRequest reapplyRequest){
        boolean reapply = tutorService.reapply(openid, reapplyRequest);
        if(reapply){
            // todo 通知管理员审核

            return SingleResult.success(true);
        }else {
            return SingleResult.error(CodeMsg.TUTOR_FAIL_REGISTER);
        }
    }

    @PostMapping("/update")
    @MethodLog("修改导师工作信息")
    public SingleResult<Boolean> update_info(String openid,
                                             @Validated @RequestBody TutorUpdateRequest request){

        Integer consultationType = request.getConsultationType();
        if(ConsultationTypeEnum.KAO_YAN.getCode().equals(consultationType)){
            checkParam(request);
        }

        boolean update = tutorService.update(openid, request);
        if(update){
            return SingleResult.success(true);
        }else {
            return SingleResult.error(CodeMsg.FAIL_UPDATE_TUTOR);
        }

    }

    @PostMapping("/upload_image")
    @MethodLog("上传审核文件")
    public SingleResult<String> upload (@RequestBody MultipartFile file){

        if(ObjectUtils.isEmpty(file) || file.isEmpty()){
            return SingleResult.error(CodeMsg.FAIL_UPLOAD);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "." + FilenameUtils.getExtension(originalFilename);
        if(!"PNG".equals(extension.toUpperCase()) && !"JPG".equals(extension.toUpperCase()) && !"JPEG".equals(extension.toUpperCase())){
            return SingleResult.error(CodeMsg.ERROR_EXTENSION);
        }
        String fileName = UUIDUtil.getImageFileName();
        String realName = fileName+extension;

        try {
            String url = aliyunService.uploadFile(AppContantConfig.ALIYUN_OSS_IMAGES_FOLDER, realName, file.getInputStream());
            return SingleResult.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return SingleResult.error(CodeMsg.FAIL_UPLOAD);
        }
    }

    private List<ReserveOrderInfoVO> buildReserveVO(IPage<Map<String, Object>> orderList) {
        return orderList.getRecords().stream().map(map -> {
            ReserveOrderInfoVO vo = new ReserveOrderInfoVO();
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

        Timestamp startTime = (Timestamp) voMap.get("reserveStartTime");
        Timestamp endTime = (Timestamp) voMap.get("reserveEndTime");
        vo.setReserveDate(startTime.toLocalDateTime().toLocalDate());
        vo.setReserveStartTime(startTime.toLocalDateTime().toLocalTime());
        vo.setReserveEndTime(endTime.toLocalDateTime().toLocalTime());
        return vo;
    }

    private void checkParam(BaseTutorInfo request){
        if(null == request.getFirstScore()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("初试成绩不能为空"));
        }
        if(null == request.getFirstRank() || null == request.getFirstTotal()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("初试排名不能为空"));
        }
        if(request.getFirstRank() > request.getFirstTotal()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("初试排名不能大于初试总人数"));
        }
        if(null == request.getSecondScore()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("复试成绩不能为空"));
        }
        if(null == request.getSecondRank() || null == request.getSecondTotal()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("复试排名不能为空"));
        }
        if(request.getSecondRank() > request.getSecondTotal()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("复试排名不能大于复试总人数"));
        }
    }

    private void checkWorkTime(BaseTutorInfo request) {
        List<TimePair> workTimeList = request.getWorkTimeList();
        boolean flag = true;
        for (TimePair timePair : workTimeList){
            flag = flag && DateUtil.equalZero(timePair.getStartTime(),timePair.getEndTime());
            if(!flag){
                break;
            }
        }
        if(flag){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("工作时间不能都设为空"));
        }
    }

}
