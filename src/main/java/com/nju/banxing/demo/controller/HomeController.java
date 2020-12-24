package com.nju.banxing.demo.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.TimePair;
import com.nju.banxing.demo.domain.CommentDO;
import com.nju.banxing.demo.domain.TutorDO;
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

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;


    @GetMapping("/tutor_list")
    @MethodLog("获取首页列表")
    public PagedResult<TutorSimpleInfoVO> list(TutorListQuery query){
        Integer type = query.getConsultationType();
        if(ObjectUtils.isEmpty(type) || (ObjectUtils.isNotEmpty(type) && type != 1 && type != 2)){
            type = 1;
        }
        IPage<TutorDO> page = tutorService.getAll(type, query.getKeyword(),query.getPageIndex(),query.getPageSize());
        List<TutorSimpleInfoVO> voList = page.getRecords().stream().map(tutorDO -> {
            TutorSimpleInfoVO tutorSimpleInfoVO = new TutorSimpleInfoVO();
            BeanUtils.copyProperties(tutorDO, tutorSimpleInfoVO);
            tutorSimpleInfoVO.setOpenid(tutorDO.getId());
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

    @GetMapping("/comment_list")
    @MethodLog("获取评论列表")
    public PagedResult<CommentVO> getComment(CommentListQuery query){
        Integer type = query.getConsultationType();
        if(ObjectUtils.isEmpty(type) || (ObjectUtils.isNotEmpty(type) && type != 1 && type != 2)){
            type = 1;
        }

        IPage<CommentDO> page = commentService.getAll(type, query.getTutorId(), query.getPageIndex(), query.getPageSize());
        List<CommentVO> list = page.getRecords().stream().map(commentDO -> {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(commentDO, commentVO);
            commentVO.setNickName(userService.getNickNameById(commentDO.getUserId()));
            commentVO.setCommentTimeStamp(DateUtil.toTimeStamp(commentDO.getCommentTime()));
            return commentVO;
        }).collect(Collectors.toList());

        return PagedResult.success(list,page.getCurrent(),page.getSize(),page.getTotal(),page.getPages());
    }



    private TutorDetailInfoVO buildVO(TutorDO tutorDO){
        TutorDetailInfoVO vo = new TutorDetailInfoVO();
        BeanUtils.copyProperties(tutorDO,vo);
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

}
