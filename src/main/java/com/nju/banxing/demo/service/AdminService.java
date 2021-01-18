package com.nju.banxing.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nju.banxing.demo.domain.UserRoleDO;
import com.nju.banxing.demo.domain.mapper.UserRoleMapper;
import com.nju.banxing.demo.enums.UserRoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 管理员服务
 * @Date: 2021/1/18
 */
@Service
public class AdminService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public boolean setAdmin(String openid){
        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setCreator(openid);
        userRoleDO.setModifier(openid);
        userRoleDO.setUserId(openid);
        userRoleDO.setRoleId(UserRoleEnum.ROOT.getCode());
        return userRoleMapper.insert(userRoleDO) > 0;
    }

    public boolean isAdmin(String openid){
        List<UserRoleDO> userRoleDOS = userRoleMapper.selectList(new QueryWrapper<UserRoleDO>().lambda().eq(UserRoleDO::getUserId, openid));
        for (UserRoleDO userRoleDO : userRoleDOS){
            if (UserRoleEnum.ROOT.getCode().equals(userRoleDO.getRoleId())){
                return true;
            }
        }
        return false;
    }

}
