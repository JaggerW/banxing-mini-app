package com.nju.banxing.demo.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.TimePair;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.domain.TutorDO;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public SingleResult<ReserveVO> toReserve(@RequestParam(value = "tutorId") String tutorId){

        TutorDO tutorDO = tutorService.getById(tutorId);
        if(ObjectUtils.isEmpty(tutorDO)){
            return SingleResult.error(CodeMsg.NULL_TUTOR);
        }

        ReserveVO reserveVO = buildReserveVO(tutorDO);
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
                workTimeVO.setDateTimeStamp(DateUtil.toTimeStamp(dateTime.toLocalDate()));
                workTimeVO.setDate(dateTime.toLocalDate());
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
            boolean b = orderService.initOrder(openid, orderCode,consultationCost, totalCost, request);
            if(b){
                return SingleResult.success(payOrder);
            }else {
                log.error("数据库初始化订单失败");
                return SingleResult.error(CodeMsg.ERROR_ORDER);
            }
        } catch (WxPayException e) {
            log.error("微信支付统一下单接口调用异常");
            deleteRedis(request.getDupKey());
            throw new GlobalException(CodeMsg.FAIL_PAY_ERROR_ORDER);
        }
    }

    @GetMapping("/query_status")
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

    private ReserveVO buildReserveVO(TutorDO tutorDO){
        ReserveVO reserveVO = new ReserveVO();
        reserveVO.setConsultationCost(tutorDO.getConsultationCost());
        reserveVO.setConsultationType(tutorDO.getConsultationType());
        reserveVO.setCurrentProfession(tutorDO.getCurrentProfession());
        reserveVO.setCurrentUniversity(tutorDO.getCurrentUniversity());
        reserveVO.setNickName(tutorDO.getNickName());

        // 设置时间
        String workTime = tutorDO.getWorkTime();
        log.info("==== RESOLVE WORK TIME FROM JSON_STRING");
        Map<Integer, TimePair> timePairMap = JSON.parseArray(workTime, TimePair.class).stream().collect(Collectors.toMap(TimePair::getKey, Function.identity()));
        log.info("==== WORK TIME MAP IS : " + timePairMap.toString());
        LocalTime startTime = LocalTime.of(0,0,0,0);
        LocalTime entTime = LocalTime.of(0,0,0,0);
        int dayKey = DateUtil.now().getDayOfWeek().getValue();
        for (int i = 0; i < 7; i++) {
            dayKey = (dayKey -1 + i)%7+1;
            TimePair timePair = timePairMap.get(dayKey);
            boolean b = DateUtil.equalZero(timePair.getStartTime(), timePair.getEndTime());
            if(!b){
                startTime = timePair.getStartTime();
                entTime = timePair.getEndTime();
                break;
            }
        }
        reserveVO.setStartTime(startTime);
        reserveVO.setStartTimeSecondOfDay(DateUtil.getSecond(startTime));

        reserveVO.setEndTime(entTime);
        reserveVO.setEndTimeSecondOfDay(DateUtil.getSecond(entTime));

        LocalDateTime dateTime = DateUtil.getNextDayOfWeek(dayKey);
        reserveVO.setDate(dateTime.toLocalDate());
        reserveVO.setDateTimeStamp(DateUtil.toTimeStamp(dateTime.toLocalDate()));

        reserveVO.setKey(dayKey);
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
        request.setNotifyUrl(AppContantConfig.SERVER_PATH_PREFIX + "/pay/order_notify");
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

}
