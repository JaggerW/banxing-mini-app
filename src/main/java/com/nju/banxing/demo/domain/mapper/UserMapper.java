package com.nju.banxing.demo.domain.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nju.banxing.demo.domain.UserDO;
import org.apache.ibatis.annotations.Param;


/**
 * @Author: jaggerw
 * @Description: 用户数据库层接口
 * @Date: 2020/11/5
 */
public interface UserMapper extends BaseMapper<UserDO> {

    int updateUserLog(@Param("openid") String openid);

    String getNickNameById(@Param("openid") String openid);

}
