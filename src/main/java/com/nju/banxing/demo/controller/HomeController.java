package com.nju.banxing.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.domain.TutorDO;
import com.nju.banxing.demo.request.HomePageQuery;
import com.nju.banxing.demo.service.TutorService;
import com.nju.banxing.demo.vo.TutorSimpleInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
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


    @GetMapping("/list")
    @MethodLog("获取首页列表")
    public PagedResult<TutorSimpleInfoVO> list(HomePageQuery query){
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

}
