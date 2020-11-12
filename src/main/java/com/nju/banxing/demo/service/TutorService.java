package com.nju.banxing.demo.service;

import ch.qos.logback.classic.turbo.TurboFilter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nju.banxing.demo.domain.TutorDO;
import com.nju.banxing.demo.domain.mapper.TutorMapper;
import com.nju.banxing.demo.request.TutorRegisterRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 导师表 服务类
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-11
 */
@Service
public class TutorService {

    @Autowired
    private TutorMapper tutorMapper;

    /**
     * 申请导师
     * @param openid
     * @param request
     * @return
     */
    public boolean register(String openid, TutorRegisterRequest request) {
        TutorDO tutorDO = new TutorDO();
        BeanUtils.copyProperties(request,tutorDO);
        tutorDO.setId(openid);
        tutorDO.setWorkTime(JSON.toJSONString(request.getWorkTimeList()));
        tutorDO.setKeyword(request.getCurrentUniversity()+request.getCurrentProfession());
        tutorDO.setCreator(openid);
        tutorDO.setModifier(openid);
        return tutorMapper.insert(tutorDO) > 0;
    }

    /**
     * 获取审核状态
     * @param openid
     * @return
     */
    public int getStatus(String openid){
        List<Map<String, Object>> maps = tutorMapper.selectMaps(
                new QueryWrapper<TutorDO>().lambda()
                        .select(TutorDO::getStatus)
                        .eq(TutorDO::getId, openid));
        return (int) maps.get(0).get("status");
    }
}
