package com.nju.banxing.demo.service;

import com.nju.banxing.demo.common.wx.WxSessionInfo;
import com.nju.banxing.demo.domain.UserDO;
import com.nju.banxing.demo.domain.mapper.UserMapper;
import com.nju.banxing.demo.mw.redis.UserRedisKeyPrefix;
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
        return userMapper.getById(openid);
    }

    public UserDO getByToken(String token) {
        String openid = getOpenidByToken(token);
        if (!StringUtils.isEmpty(openid)) {
            return getById(openid);
        }
        return null;
    }

    public UserDO insertUser(UserDO userDO) {
        // TODO 1 插入成功
        userMapper.insert(userDO);
        return userMapper.getById(userDO.getOpenid());
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
