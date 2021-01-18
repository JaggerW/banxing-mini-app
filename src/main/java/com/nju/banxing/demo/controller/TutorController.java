package com.nju.banxing.demo.controller;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.alibaba.fastjson.JSONArray;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.TimePair;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.domain.OrderDO;
import com.nju.banxing.demo.domain.TutorDO;
import com.nju.banxing.demo.domain.TutorScoreInfo;
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
import com.nju.banxing.demo.util.TXMeetingUtil;
import com.nju.banxing.demo.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.SocketHandler;

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
    private WeixinService weixinService;

    @GetMapping("/get_status")
    @MethodLog("获取导师注册申请状态")
    public SingleResult<Integer> getStatus(String openid){

        int status = tutorService.getStatus(openid);
        return SingleResult.success(status);

    }

    @GetMapping("/get_base_info")
    @MethodLog("获取导师的基本信息")
    public SingleResult<TutorBaseInfoVO> getBaseInfo(String openid){
        TutorDO tutorDO = tutorService.getById(openid);
        TutorBaseInfoVO tutorBaseInfoVO = buildTutorBaseInfoVO(tutorDO);
        return SingleResult.success(tutorBaseInfoVO);
    }

    @GetMapping("/get_apply_info")
    @MethodLog("获取导师的注册信息")
    public SingleResult<TutorApplyInfoVO> getApplyInfo(String openid){
        Map<String, Object> applyInfoById = tutorService.getApplyInfoById(openid);
        TutorApplyInfoVO tutorApplyInfoVO = buildApplyInfoVO(applyInfoById);
        return SingleResult.success(tutorApplyInfoVO);
    }

    @PostMapping("/handle_order")
    @MethodLog("导师提交预约申请处理结果")
    public SingleResult<String> handleOrder(String openid,
                                            @Validated @RequestBody TutorHandleOrderRequest request){
        if(ObjectUtils.isEmpty(request)){
            return SingleResult.error(CodeMsg.BIND_ERROR.fillArgs("request不得为空"));
        }
        String userId = orderService.getUserIdByOrderCode(request.getOrderCode());
        if(StringUtils.isEmpty(userId)){
            throw new GlobalException(CodeMsg.NULL_ORDER);
        }

        if(TutorStatusEnum.ACCEPTED.getCode().equals(request.getHandleType())){
            // 同意
            // 校验参数
            TXMeetingInfoVO TXMeetingInfoVO = TXMeetingUtil.parseMes(request.getContent());
            checkMesVO(TXMeetingInfoVO,request);

            // 更新订单
            boolean accept = tutorService.accept(openid, request, TXMeetingInfoVO.getMeetingUrl());
            if(accept){
                // 通知学员
                sendSuccessWxMes(request.getOrderCode(),userId, TXMeetingInfoVO);

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
            WxRefundRequest refundRequest = buildRefundRequest(openid, request.getOrderCode());
            try {
                WxRefundVO wxRefundVO = weixinService.applyRefund(refundRequest);

                boolean reject = tutorService.reject(openid, request, wxRefundVO);
                if(reject){
                    // 通知学员
                    sendRejectWxMes(userId,content);

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

    @PostMapping("/register")
    @MethodLog("申请注册导师")
    public SingleResult<Boolean> register(String openid,
                                          @Validated @RequestBody TutorRegisterRequest request){

        checkParam(request);

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

        checkParam(request);

        boolean update = tutorService.update(openid, request);
        if(update){
            return SingleResult.success(true);
        }else {
            return SingleResult.error(CodeMsg.FAIL_UPDATE_TUTOR);
        }

    }

    @PostMapping("/upload_image")
    @MethodLog("上传审核文件")
    public SingleResult<String> upload (@RequestBody MultipartFile image){

        if(ObjectUtils.isEmpty(image) || image.isEmpty()){
            return SingleResult.error(CodeMsg.FAIL_UPLOAD);
        }

        String originalFilename = image.getOriginalFilename();
        String extension = "." + FilenameUtils.getExtension(originalFilename);
        if(!".PNG".equals(extension.toUpperCase()) && !".JPG".equals(extension.toUpperCase()) && !".JPEG".equals(extension.toUpperCase())){
            return SingleResult.error(CodeMsg.ERROR_EXTENSION);
        }
        String fileName = UUIDUtil.getImageFileName();
        String realName = fileName+extension;

        try {
            String url = aliyunService.uploadFile(AppContantConfig.ALIYUN_OSS_IMAGES_FOLDER, realName, image.getInputStream());
            return SingleResult.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return SingleResult.error(CodeMsg.FAIL_UPLOAD);
        }
    }


    private WxRefundRequest buildRefundRequest(String tutorId, String orderCode) {
        OrderDO orderDO = orderService.getByOrderCodeAndTutorId(orderCode, tutorId);
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

    private void sendSuccessWxMes(String orderCode, String openid, TXMeetingInfoVO TXMeetingInfoVO) {

        OrderDO orderDO = orderService.getByOrderCodeAndTutorId(orderCode, openid);
        String userId = orderDO.getUserId();
        Integer consultationType = orderDO.getConsultationType();
        String typeName = ConsultationTypeEnum.getEnumByCode(consultationType).getName();

        List<WxMaSubscribeMessage.Data> dataList = weixinService.getMeetingSuccessWxMessage(typeName, TXMeetingInfoVO.getMeetingTime(), TXMeetingInfoVO.getMeetingId(), TXMeetingInfoVO.getMeetingSecret());
        weixinService.sendWxMessage(userId,AppContantConfig.WX_MSG_MEETING_SUCCESS_TEMPLATE_ID,AppContantConfig.WX_MSG_MEETING_SUCCESS_PAGE,dataList);

    }

    private void sendRejectWxMes(String openid, String reason){
        List<WxMaSubscribeMessage.Data> dataList = weixinService.getMeetingRejectWxMessage(reason);
        weixinService.sendWxMessage(openid,AppContantConfig.WX_MSG_MEETING_REJECT_TEMPLATE_ID,AppContantConfig.WX_MSG_MEETING_REJECT_PAGE,dataList);
    }

    private void checkMesVO(TXMeetingInfoVO TXMeetingInfoVO, TutorHandleOrderRequest request) {

        if(ObjectUtils.isEmpty(TXMeetingInfoVO)){
            throw new GlobalException(CodeMsg.ERROR_MEETING_MESSAGE);
        }
        if(StringUtils.isEmpty(TXMeetingInfoVO.getMeetingId())){
            throw new GlobalException(CodeMsg.ERROR_MEETING_MESSAGE);
        }
        if(StringUtils.isEmpty(TXMeetingInfoVO.getMeetingUrl())){
            throw new GlobalException(CodeMsg.ERROR_MEETING_MESSAGE);
        }
        if(StringUtils.isEmpty(TXMeetingInfoVO.getMeetingTime())){
            throw new GlobalException(CodeMsg.ERROR_MEETING_MESSAGE);
        }
        try {
            LocalDateTime meetingStartTime = TXMeetingInfoVO.getMeetingStartTime();
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

    private TutorApplyInfoVO buildApplyInfoVO(Map<String, Object> map) {
        TutorApplyInfoVO vo = new TutorApplyInfoVO();
        vo.setCurrentProfession((String) map.get("currentProfession"));
        vo.setCurrentUniversity((String) map.get("currentUniversity"));
        vo.setStudentCardHome((String) map.get("studentCardHome"));
        vo.setStudentCardInfo((String) map.get("studentCardInfo"));
        vo.setStudentCardRegister((String) map.get("studentCardRegister"));
        return vo;
    }

    private TutorBaseInfoVO buildTutorBaseInfoVO(TutorDO tutorDO) {
        TutorBaseInfoVO vo = new TutorBaseInfoVO();
        BeanUtils.copyProperties(tutorDO,vo);
        TutorScoreInfo scoreInfo = tutorDO.getScoreInfo();
        vo.setFirstRank(scoreInfo.getFirstRank());
        vo.setFirstScore(scoreInfo.getFirstScore());
        vo.setFirstTotal(scoreInfo.getFirstTotal());
        vo.setSecondRank(scoreInfo.getSecondRank());
        vo.setSecondScore(scoreInfo.getSecondScore());
        vo.setSecondTotal(scoreInfo.getSecondTotal());
        vo.setGpa(scoreInfo.getGpa());
        vo.setGpaRank(scoreInfo.getGpaRank());
        vo.setGpaTotal(scoreInfo.getGpaTotal());
        vo.setMaxGPA(scoreInfo.getMaxGPA());
        String workTime = tutorDO.getWorkTime();
        List<TimePair> timePairs = JSONArray.parseArray(workTime, TimePair.class);
        vo.setWorkTimeList(timePairs);
        return vo;
    }

    private void checkParam(BaseTutorInfo request){
        // 如果是考研
        if(ConsultationTypeEnum.KAO_YAN.getCode().equals(request.getConsultationType())){

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

        // 如果是保研
        if(ConsultationTypeEnum.BAO_YAN.getCode().equals(request.getConsultationType())){

            if(null == request.getGpa()){
                throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("学分绩点不能为空"));
            }
            if(null == request.getMaxGPA()){
                throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("总绩点不能为空"));
            }
            if(null == request.getGpaRank() || null == request.getGpaTotal()){
                throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("专业排名不能为空"));
            }
            if(request.getGpaRank() > request.getGpaTotal()){
                throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("专业排名不能大于专业总人数"));
            }
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
