package com.nju.banxing.demo.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nju.banxing.demo.domain.TutorDO;
import com.nju.banxing.demo.domain.UserDO;
import com.nju.banxing.demo.domain.mapper.TutorMapper;
import com.nju.banxing.demo.domain.mapper.UserMapper;
import com.nju.banxing.demo.enums.TutorStatusEnum;
import com.nju.banxing.demo.request.TutorRegisterRequest;
import com.nju.banxing.demo.vo.TutorSimpleInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
public class TutorService {

    @Autowired
    private TutorMapper tutorMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 申请导师
     *
     * @param openid
     * @param request
     * @return
     */
    public boolean register(String openid, TutorRegisterRequest request) {
        TutorDO tutorDO = new TutorDO();
        BeanUtils.copyProperties(request, tutorDO);
        tutorDO.setId(openid);
        tutorDO.setWorkTime(JSON.toJSONString(request.getWorkTimeList()));
        tutorDO.setKeyword(request.getCurrentUniversity() + request.getCurrentProfession());
        tutorDO.setCreator(openid);
        tutorDO.setModifier(openid);

        List<Map<String, Object>> maps = userMapper.selectMaps(
                new QueryWrapper<UserDO>().lambda()
                        .select(UserDO::getNickName)
                        .eq(UserDO::getId, openid));
        String nickName = (String) maps.get(0).get("nickName");
        tutorDO.setNickName(nickName);

        return tutorMapper.insert(tutorDO) > 0;
    }

    /**
     * 获取审核状态
     *
     * @param openid
     * @return
     */
    public int getStatus(String openid) {
        List<Map<String, Object>> maps = tutorMapper.selectMaps(
                new QueryWrapper<TutorDO>().lambda()
                        .select(TutorDO::getStatus)
                        .eq(TutorDO::getId, openid));
        return (int) maps.get(0).get("status");
    }


    /**
     * 查询
     * @param type
     * @param keyword
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public IPage<TutorDO> getAll(Integer type, String keyword, Long pageIndex, Long pageSize) {

        Page<TutorDO> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<TutorDO> queryWrapper = new QueryWrapper<TutorDO>().lambda()
                .eq(TutorDO::getStatus, TutorStatusEnum.VERIFY_PASS.getCode())
                .eq(TutorDO::getConsultationType, type)
                .and(StringUtils.isNotEmpty(keyword),
                        qw -> qw.like(TutorDO::getNickName, keyword).or()
                                .like(TutorDO::getKeyword, keyword).or()
                                .like(TutorDO::getCurrentUniversity, keyword).or()
                                .like(TutorDO::getCurrentProfession, keyword));
        return tutorMapper.selectPage(page, queryWrapper);
    }
}
