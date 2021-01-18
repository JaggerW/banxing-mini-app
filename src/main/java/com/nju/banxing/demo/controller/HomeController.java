package com.nju.banxing.demo.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.TimePair;
import com.nju.banxing.demo.domain.CommentDO;
import com.nju.banxing.demo.domain.TutorDO;
import com.nju.banxing.demo.enums.ConsultationTypeEnum;
import com.nju.banxing.demo.enums.DayOfWeekEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.request.CommentListQuery;
import com.nju.banxing.demo.request.TutorListQuery;
import com.nju.banxing.demo.service.CommentService;
import com.nju.banxing.demo.service.TutorService;
import com.nju.banxing.demo.service.UserService;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.vo.CommentVO;
import com.nju.banxing.demo.vo.TutorDetailInfoVO;
import com.nju.banxing.demo.vo.TutorSimpleInfoVO;
import com.nju.banxing.demo.vo.WorkTimeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: jaggerw
 * @Description: 首页
 * @Date: 2020/11/13
 */
@RestController
@RequestMapping("/home")
@Slf4j
public class HomeController {


    @Autowired
    private TutorService tutorService;


    @GetMapping("/tutor_list")
    @MethodLog("获取首页列表")
    public PagedResult<TutorSimpleInfoVO> list(TutorListQuery query){
        Integer type = query.getConsultationType();
        if(ObjectUtils.isEmpty(type)
                || (ObjectUtils.isNotEmpty(type)
                    && !ConsultationTypeEnum.KAO_YAN.getCode().equals(type)
                    && !ConsultationTypeEnum.BAO_YAN.getCode().equals(type))){
            type = ConsultationTypeEnum.KAO_YAN.getCode();
        }
        IPage<TutorDO> page = tutorService.getAll(type, query.getKeyword(),query.getPageIndex(),query.getPageSize());
        List<TutorSimpleInfoVO> voList = page.getRecords().stream().map(tutorDO -> {
            TutorSimpleInfoVO tutorSimpleInfoVO = new TutorSimpleInfoVO();
            BeanUtils.copyProperties(tutorDO, tutorSimpleInfoVO);
            tutorSimpleInfoVO.setTutorScoreInfo(tutorDO.getScoreInfo());
            tutorSimpleInfoVO.setOpenid(tutorDO.getId());
            Float score = calCommentScore(tutorDO);
            tutorSimpleInfoVO.setCommentScore(score);
            return tutorSimpleInfoVO;
        }).collect(Collectors.toList());

        return PagedResult.success(voList,page.getCurrent(),page.getSize(),page.getTotal(),page.getPages());
    }

    @GetMapping("/tutor_detail")
    @MethodLog("获取导师详细信息")
    public SingleResult<TutorDetailInfoVO> detail(@RequestParam(value = "tutorId") String tutorId){
        if(StringUtils.isEmpty(tutorId)){
            return SingleResult.error(CodeMsg.BIND_ERROR.fillArgs("tutorId不能为空"));
        }

        TutorDO tutorDO = tutorService.getById(tutorId);
        TutorDetailInfoVO detail = buildVO(tutorDO);
        if(ObjectUtils.isEmpty(detail)){
            return SingleResult.error(CodeMsg.NULL_TUTOR);
        }
        return SingleResult.success(detail);
    }



    private TutorDetailInfoVO buildVO(TutorDO tutorDO){
        TutorDetailInfoVO vo = new TutorDetailInfoVO();
        BeanUtils.copyProperties(tutorDO,vo);
        vo.setTutorScoreInfo(tutorDO.getScoreInfo());
        Float score = calCommentScore(tutorDO);
        vo.setCommentScore(score);
        vo.setOpenid(tutorDO.getId());
        String workTime = tutorDO.getWorkTime();
        List<TimePair> timePairList = JSON.parseArray(workTime, TimePair.class);
        List<WorkTimeVO> workTimeVOList = timePairList.stream().map(timePair -> {
            WorkTimeVO workTimeVO = new WorkTimeVO();
            LocalTime startTime = timePair.getStartTime();
            LocalTime endTime = timePair.getEndTime();
            workTimeVO.setKey(timePair.getKey());
            workTimeVO.setStartTime(startTime);
            workTimeVO.setEndTime(endTime);
            workTimeVO.setStartTimeSecondOfDay(DateUtil.getSecond(startTime));
            workTimeVO.setEndTimeSecondOfDay(DateUtil.getSecond(endTime));
            workTimeVO.setDayOfWeek(Objects.requireNonNull(DayOfWeekEnum.getEnumByCode(timePair.getKey())).getDesc());
            boolean b = DateUtil.equalZero(startTime, endTime);
            workTimeVO.setReserveFlag(!b);
            return workTimeVO;
        }).collect(Collectors.toList());
        vo.setWorkTimeList(workTimeVOList);
        return vo;
    }

    private Float calCommentScore(TutorDO tutorDO){
        Integer count = tutorDO.getConsultationCount();
        Float commentScore = tutorDO.getCommentScore();
        return commentScore / count;
    }

}
