package com.nju.banxing.demo.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.TimePair;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.domain.TutorDO;
import com.nju.banxing.demo.enums.DayOfWeekEnum;
import com.nju.banxing.demo.enums.TutorStatusEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.service.AliyunService;
import com.nju.banxing.demo.service.TutorService;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.vo.ReserveVO;
import com.nju.banxing.demo.vo.TutorDetailInfoVO;
import com.nju.banxing.demo.vo.WorkTimeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("to_reserve")
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

    @GetMapping("get_day")
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

    // TODO 下单，付款技术方案
//
//    @PostMapping("create")
//    @MethodLog("用户下单")
//    public SingleResult<Boolean> createOrder(){
//
//    }

    @PostMapping("/upload_pdf")
    @MethodLog("上传审核文件")
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
}
