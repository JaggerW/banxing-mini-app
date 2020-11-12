package com.nju.banxing.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nju.banxing.demo.common.wx.WxSessionInfo;
import com.nju.banxing.demo.common.wx.WxUserInfo;
import com.nju.banxing.demo.domain.UserDO;
import com.nju.banxing.demo.domain.mapper.UserMapper;
import com.nju.banxing.demo.mw.redis.UserRedisKeyPrefix;
import com.nju.banxing.demo.request.UserRegisterRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Author: jaggerw
 * @Description: 用户
 * @Date: 2020/11/5
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    public UserDO getById(String openid) {
        return userMapper.selectById(openid);
    }

    public UserDO getByToken(String token) {
        String openid = getOpenidByToken(token);
        if (!StringUtils.isEmpty(openid)) {
            return getById(openid);
        }
        return null;
    }

    /**
     * 判断用户是否已注册
     *
     * @param openid
     * @return
     */
    public Boolean existUser(String openid) {
        return userMapper.selectCount(new QueryWrapper<UserDO>().lambda().eq(UserDO::getId, openid)) > 0;
    }

    /**
     * 新建用户
     *
     * @param openid
     * @param request
     * @param info
     * @return
     */
    public boolean insertUser(String openid, UserRegisterRequest request, WxUserInfo info) {

        UserDO userDO = new UserDO();
        userDO.setId(openid);

        // 填充微信信息
        userDO.setGender(info.getGender());
        userDO.setAvatarUrl(info.getAvatarUrl());
        userDO.setCity(info.getCity());
        userDO.setProvince(info.getProvince());
        userDO.setCountry(info.getCountry());

        // 填充注册信息
        userDO.setNickName(request.getNickName());
        userDO.setMobile(request.getMobile());
        userDO.setEmail(request.getEmail());

        // 设置咨询类型，可多选
        int consultationType = 0;
        for (int type : request.getConsultationTypeList()) {
            consultationType |= type;
        }
        userDO.setConsultationType(consultationType);

        // 默认值
        userDO.setCreator(openid);
        userDO.setModifier(openid);

//        Date curDate = DateUtil.getCurrentDate();
//        userDO.setAdminFlag(false);
//        userDO.setCreateTime(curDate);
//        userDO.setCreator(openid);
//        userDO.setEmailPermission(true);
//        userDO.setLatestLoginTime(curDate);
//        userDO.setLoginCount(1L);
//        userDO.setModifier(openid);
//        userDO.setModifyTime(curDate);
//        userDO.setRegisterTime(curDate);
//        userDO.setSmsPermission(true);
//        userDO.setTutorFlag(false);
//        userDO.setExtension(null);

        return userMapper.insert(userDO) > 0;
    }

    /**
     * 更新用户登录日志
     * @param openid
     * @return
     */
    public boolean updateUserLog(String openid){
        return userMapper.updateUserLog(openid) > 0;
    }

    public Boolean existToken(String token) {
        if (StringUtils.isEmpty(token) || !redisService.exists(UserRedisKeyPrefix.userToken, token)) {
            return false;
        }
        redisService.updateExpire(UserRedisKeyPrefix.userToken, token);
        return true;
    }

    public String getOpenidByToken(String token) {
        WxSessionInfo sessionInfo = redisService.get(UserRedisKeyPrefix.userToken, token, WxSessionInfo.class);
        if (null != sessionInfo) {
            return sessionInfo.getOpenId();
        }
        return null;
    }

}
