package com.nju.banxing.demo.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.TimePair;
import com.nju.banxing.demo.common.sms.LoginVerSmsTemplate;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.domain.TutorDO;
import com.nju.banxing.demo.enums.OrderStatusEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.mw.redis.OrderRedisKeyPrefix;
import com.nju.banxing.demo.request.OrderCreateRequest;
import com.nju.banxing.demo.request.WxPayOrderRequest;
import com.nju.banxing.demo.service.*;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.MathUtil;
import com.nju.banxing.demo.util.NetworkUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.vo.AliyunSmsVO;
import com.nju.banxing.demo.vo.ReserveVO;
import com.nju.banxing.demo.vo.WorkTimeVO;
import com.nju.banxing.demo.vo.WxPayOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * @Author: jaggerw
 * @Description: 订单
 * @Date: 2020/11/14
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private TutorService tutorService;

    @Autowired
    private AliyunService aliyunService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/to_reserve")
    @MethodLog("打开预约界面")
    public SingleResult<ReserveVO> toReserve(@RequestParam(value = "tutorId") String tutorId,
                                             @RequestParam(value = "dayKey") Integer day){

        TutorDO tutorDO = tutorService.getById(tutorId);
        if(ObjectUtils.isEmpty(tutorDO)){
            return SingleResult.error(CodeMsg.NULL_TUTOR);
        }

        ReserveVO reserveVO = buildReserveVO(tutorDO, day);
        return SingleResult.success(reserveVO);

    }

    @GetMapping("/get_day")
    @MethodLog("获取未来十四天可选日期")
    public PagedResult<WorkTimeVO> getDay(@RequestParam(value = "tutorId") String tutorId){
        if(StringUtils.isEmpty(tutorId)){
            throw new GlobalException(CodeMsg.NULL_TUTOR);
        }

        String workTimeStr = tutorService.getWorkTimeById(tutorId);
        if(StringUtils.isEmpty(workTimeStr)){
            log.error("无法获取工作时间");
        }
        List<TimePair> timePairs = JSON.parseArray(workTimeStr, TimePair.class);
        Map<Integer, TimePair> maps = Maps.newHashMap();
        for(TimePair timePair : timePairs){
            maps.put(timePair.getKey(),timePair);
        }

        List<LocalDateTime> nextTwoWeeks = DateUtil.getNextTwoWeeks();
        List<WorkTimeVO> list = Lists.newArrayList();
        for (LocalDateTime dateTime : nextTwoWeeks){
            WorkTimeVO workTimeVO = new WorkTimeVO();
            int day = dateTime.getDayOfWeek().getValue();
            TimePair timePair = maps.get(day);
            // 判断空，但正常都有的
            if(ObjectUtils.isEmpty(timePair)){
                continue;
            }

            LocalTime startTime = timePair.getStartTime();
            LocalTime endTime = timePair.getEndTime();

            // 判断是否不可预约
            boolean b = DateUtil.equalZero(startTime, endTime);
            if(!b){
                workTimeVO.setReserveFlag(true);
                workTimeVO.setStartTime(startTime);
                workTimeVO.setEndTime(endTime);
                workTimeVO.setStartTimeSecondOfDay(DateUtil.getSecond(startTime));
                workTimeVO.setEndTimeSecondOfDay(DateUtil.getSecond(endTime));
                workTimeVO.setKey(timePair.getKey());
                workTimeVO.setDateTimeStamp(DateUtil.toTimeStamp(dateTime));
                workTimeVO.setDate(dateTime);
                workTimeVO.setDayOfWeek(DateUtil.getNameDayOfWeek(dateTime));
                list.add(workTimeVO);
            }
        }

        return PagedResult.success(list,1,list.size(),list.size(),1);
    }

    @PostMapping("/create")
    @MethodLog("用户下单")
    public SingleResult<WxPayOrderVO> createOrder(String openid,
                                                        @Validated @RequestBody OrderCreateRequest request,
                                                        HttpServletRequest httpServletRequest){

        if(ObjectUtils.isEmpty(request)) {
            return SingleResult.error(CodeMsg.BIND_ERROR.fillArgs("请求体为空！"));
        }

        // 订单判重
        Boolean success = redisService.setnx(OrderRedisKeyPrefix.dupKey, request.getDupKey(),request.getDupKey());
        if(!success){
            return SingleResult.error(CodeMsg.DUP_ORDER);
        }

        // 参数校验
        checkParam(request);

        // 生成订单号
        String orderCode = UUIDUtil.getOrderCode();

        // 生成总费用
        String tutorId = request.getTutorId();
        BigDecimal consultationCost = tutorService.getConsultationCost(tutorId);
        BigDecimal totalCost = consultationCost.multiply(new BigDecimal(request.getConsultationTimeCount()));

        try {
            // 发起微信支付
            WxPayOrderRequest orderRequest = buildOrderRequest(openid, request, orderCode, totalCost, httpServletRequest);
            WxPayOrderVO payOrder = weixinService.createPayOrder(orderRequest);
            payOrder.setOrderCode(orderCode);

            log.info(payOrder.toString());

            // 订单初始化
            boolean b = orderService.initOrder(openid, orderCode, totalCost, request);
            if(b){
                return SingleResult.success(payOrder);
            }else {
                log.error("数据库初始化订单失败");
                return SingleResult.error(CodeMsg.ERROR_ORDER);
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            log.error("微信支付统一下单接口调用异常");
            deleteRedis(request.getDupKey());
            throw new GlobalException(CodeMsg.FAIL_PAY_ERROR_ORDER);
        }
    }

    @PostMapping("/notify")
    @MethodLog("微信支付回调方法")
    public String parseOrderNotifyResult(@RequestBody String xmlData) {
        try {
            WxPayOrderNotifyResult result = weixinService.notifyOrderResult(xmlData);
            checkPayResult(result);
            // 判断是否已处理过
            Map<String, Integer> map = orderService.getStatusAndVersionByCode(result.getOutTradeNo());
            if(ObjectUtils.isEmpty(map)){
                return WxPayNotifyResponse.fail("不存在该订单数据");
            }
            Integer status = map.get("status");
            Integer version = map.get("version");
            if(OrderStatusEnum.ORDER_TO_PAY.getCode().equals(status) ||
                    OrderStatusEnum.ORDER_FAIL_PAY.getCode().equals(status)){

                if(OrderStatusEnum.ORDER_FAIL_PAY.getCode().equals(status) && "FAIL".equals(result.getResultCode())){
                    // 已处理过
                    return WxPayNotifyResponse.success("成功");
                }

                // 验证金额是否一致防止伪造通知
                boolean b = checkPayCost(result);
                if(!b){
                    log.error("支付金额校验错误");
                    return WxPayNotifyResponse.fail("支付金额校验错误");
                }

                // 处理
                if("SUCCESS".equals(result.getResultCode())){
                    for (int i = 0; i < 10; i++) {
                        // 支付成功
                        // 乐观锁
                        boolean successPay = orderService.successPay(result, status, version);
                        if(successPay){
                            // TODO 短信通知导师，需要企业申请
                            String verCode = "123456";
                            String mobile = orderService.getTutorMobileByOrderCode(result.getOutTradeNo());
                            // 阿里云发送短信
                            AliyunSmsVO aliyunSmsVO = new AliyunSmsVO();
                            aliyunSmsVO.setPhoneNumber(mobile);
                            aliyunSmsVO.setSignName(AppContantConfig.ALIYUN_LOGIN_VERIFICATION_SMS_SIGN_NAME);
                            aliyunSmsVO.setTemplateCode(AppContantConfig.ALIYUN_LOGIN_VERIFICATION_SMS_TEMPLATE_CODE);

                            LoginVerSmsTemplate template = new LoginVerSmsTemplate();
                            template.setCode(verCode);
                            aliyunSmsVO.setTemplateParam(JSON.toJSONString(template));
                            aliyunService.sendSMS(aliyunSmsVO);
                            break;
                        }
                    }

                }else {

                    // 支付失败
                    // 乐观锁
                    for (int i = 0; i < 10; i++) {
                        boolean failPay = orderService.failPay(result, status, version);
                        if(failPay){
                            break;
                        }
                    }
                }
            }
            return WxPayNotifyResponse.success("成功");
        } catch (WxPayException e) {
            e.printStackTrace();
            return WxPayNotifyResponse.fail("参数校验错误");
        } catch (GlobalException e){
            log.error(e.getCodeMsg().getMsg());
            return WxPayNotifyResponse.fail("参数校验错误");
        }
    }

    @GetMapping("/query_result")
    @MethodLog("查询微信支付结果")
    public SingleResult<Integer> queryPay(@RequestParam(value = "orderCode") String orderCode){

        return SingleResult.success(orderService.getStatusByCode(orderCode));
    }

    @PostMapping("/upload_pdf")
    @MethodLog("上传简历文件")
    public SingleResult<String> upload (@RequestBody MultipartFile file){

        if(ObjectUtils.isEmpty(file) || file.isEmpty()){
            return SingleResult.error(CodeMsg.FAIL_UPLOAD);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "." + FilenameUtils.getExtension(originalFilename);
        if(!"PDF".equals(extension.toUpperCase())){
            return SingleResult.error(CodeMsg.ERROR_EXTENSION);
        }
        String fileName = UUIDUtil.getPdfFileName();
        String realName = fileName+extension;

        try {
            String url = aliyunService.uploadFile(AppContantConfig.ALIYUN_OSS_PDF_FOLDER, realName, file.getInputStream());
            return SingleResult.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return SingleResult.error(CodeMsg.FAIL_UPLOAD);
        }
    }

    private ReserveVO buildReserveVO(TutorDO tutorDO, Integer day){
        ReserveVO reserveVO = new ReserveVO();
        reserveVO.setConsultationCost(tutorDO.getConsultationCost());
        reserveVO.setConsultationType(tutorDO.getConsultationType());
        reserveVO.setCurrentProfession(tutorDO.getCurrentProfession());
        reserveVO.setCurrentUniversity(tutorDO.getCurrentUniversity());
        reserveVO.setNickName(tutorDO.getNickName());

        // 设置时间
        String workTime = tutorDO.getWorkTime();
        List<TimePair> timePairs = JSON.parseArray(workTime, TimePair.class);
        LocalTime startTime = LocalTime.of(0,0,0,0);
        LocalTime entTime = LocalTime.of(0,0,0,0);
        for(TimePair timePair : timePairs){
            if(day.equals(timePair.getKey())){
                startTime = timePair.getStartTime();
                entTime = timePair.getEndTime();
                break;
            }
        }
        reserveVO.setStartTime(startTime);
        reserveVO.setStartTimeSecondOfDay(DateUtil.getSecond(startTime));

        reserveVO.setEndTime(entTime);
        reserveVO.setEndTimeSecondOfDay(DateUtil.getSecond(entTime));

        LocalDateTime dateTime = DateUtil.getNextDayOfWeek(day);
        reserveVO.setDate(dateTime);
        reserveVO.setDateTimeStamp(DateUtil.toTimeStamp(dateTime));

        reserveVO.setKey(day);
        reserveVO.setDayOfWeek(DateUtil.getNameDayOfWeek(dateTime));

        return reserveVO;
    }

    private WxPayOrderRequest buildOrderRequest(String openid, OrderCreateRequest createRequest, String outTradeNo, BigDecimal totalCost, HttpServletRequest httpServletRequest){
        WxPayOrderRequest request = new WxPayOrderRequest();
        request.setBody("预约支付测试");
        request.setDetail("预约支付测试详情");
        request.setIp(NetworkUtil.getIpAddress(httpServletRequest));
        request.setNonceStr(UUIDUtil.getNonceStr());
        request.setOpenid(openid);
        request.setOutTradeNo(outTradeNo);
        request.setTradeType("JSAPI");
        request.setTotalTee(MathUtil.bigYuan2Fee(totalCost));
        request.setNotifyUrl(AppContantConfig.SERVER_PATH_PREFIX + "/order/notify");
        return request;
    }

    private void checkParam(OrderCreateRequest request){
        log.info("OrderCreateRequest : {}",request.toString());

        LocalDate reserveDate = DateUtil.toLocalDate(request.getReserveDateTimeStamp());
        LocalDate nowDate = DateUtil.now().toLocalDate();
        if (!reserveDate.isAfter(nowDate)){
            throw new GlobalException(CodeMsg.ERROR_RESERVE_TIME);
        }

        LocalTime calEndTime = request.getReserveStartTime().plusMinutes(10 * request.getConsultationTimeCount());

        String tutorId = request.getTutorId();
        String workTimeById = tutorService.getWorkTimeById(tutorId);
        if(StringUtils.isEmpty(workTimeById)){
            throw new GlobalException(CodeMsg.NULL_TUTOR);
        }

        int dayKey = reserveDate.getDayOfWeek().getValue();
        List<WorkTimeVO> workTimeVOS = JSON.parseArray(workTimeById, WorkTimeVO.class);
        for(WorkTimeVO workTime : workTimeVOS){
            if(workTime.getKey().equals(dayKey)){
                boolean included = DateUtil.isIncluded(request.getReserveStartTime(), calEndTime, workTime.getStartTime(), workTime.getEndTime());
                if(!included){
                    deleteRedis(request.getDupKey());
                    throw new GlobalException(CodeMsg.OUT_OF_TIME_RANGE);
                }
                break;
            }
        }
    }

    private void deleteRedis(String dupKey){
        redisService.delete(OrderRedisKeyPrefix.dupKey,dupKey);
    }

    private void checkPayResult(WxPayOrderNotifyResult result){
        if(StringUtils.isNotEmpty(result.getReturnMsg())){
            log.error(result.getReturnMsg());
            throw new GlobalException(CodeMsg.FAIL_PAY_ERROR_SIGN);
        }

        if("FAIL".equals(result.getReturnCode())){
            log.error("returnCode is fail");
            throw new GlobalException(CodeMsg.FAIL_PAY_ERROR_COM);
        }
    }

    private boolean checkPayCost(WxPayOrderNotifyResult result){
        Integer totalFee = result.getTotalFee();
        String orderCode = result.getOutTradeNo();
        BigDecimal totalCost = orderService.getTotalCostByCode(orderCode);
        int calFee = MathUtil.bigYuan2Fee(totalCost);
        log.info("微信支付回调金额为：{}; 数据库订单中支付金额为：{}",totalFee,calFee);
        return totalFee.equals(calFee);
    }

}
