package com.nju.banxing.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nju.banxing.demo.common.wx.WxSessionInfo;
import com.nju.banxing.demo.common.wx.WxUserInfo;
import com.nju.banxing.demo.domain.ReadDO;
import com.nju.banxing.demo.domain.TutorDO;
import com.nju.banxing.demo.domain.UserDO;
import com.nju.banxing.demo.domain.mapper.TutorMapper;
import com.nju.banxing.demo.domain.mapper.UserMapper;
import com.nju.banxing.demo.mw.redis.UserRedisKeyPrefix;
import com.nju.banxing.demo.request.UserRegisterRequest;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.vo.UserInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.cmp.OOBCertHash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    private TutorMapper tutorMapper;

    @Autowired
    private ReadService readService;

    @Autowired
    private RedisService redisService;

    public UserDO getById(String openid) {
        return userMapper.selectById(openid);
    }

    @Transactional
    public boolean deleteUser(String openid){
        return userMapper.deleteById(openid) > 0 && readService.delete(openid);
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
    @Transactional
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

        // 默认值
        userDO.setCreator(openid);
        userDO.setModifier(openid);

        // 插入已读记录表
        ReadDO readDO = new ReadDO();
        readDO.setId(openid);

        return userMapper.insert(userDO) > 0 && readService.insert(readDO);
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

    public UserInfoVO getUserInfo(String openid) {
        UserDO userDO = userMapper.selectById(openid);
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(userDO,vo);
        return vo;
    }

    @Transactional
    public boolean updateUserInfo(String openid, UserRegisterRequest registerRequest){
        int update = userMapper.update(null,
                new UpdateWrapper<UserDO>().lambda()
                        .eq(UserDO::getId, openid)
                        .set(UserDO::getNickName, registerRequest.getNickName())
                        .set(UserDO::getEmail, registerRequest.getEmail())
                        .set(UserDO::getMobile, registerRequest.getMobile())
                        .set(UserDO::getModifyTime, DateUtil.now())
                        .set(UserDO::getModifier,openid));

        tutorMapper.update(null,
                new UpdateWrapper<TutorDO>().lambda()
                        .eq(TutorDO::getId, openid)
                        .set(TutorDO::getNickName, registerRequest.getNickName())
                        .set(TutorDO::getModifyTime, DateUtil.now())
                        .set(TutorDO::getCreator, openid));

        return update>0;
    }

    public String getNickNameById(String openid){
        return userMapper.getNickNameById(openid);
    }

    public String getMobileById(String openid){
        return userMapper.getNickNameById(openid);
    }
}
