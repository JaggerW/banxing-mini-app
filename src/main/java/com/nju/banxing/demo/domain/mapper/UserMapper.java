package com.nju.banxing.demo.domain.mapper;


import com.nju.banxing.demo.domain.UserDO;
import org.springframework.stereotype.Repository;

/**
 * @Author: jaggerw
 * @Description: 用户数据库层接口
 * @Date: 2020/11/5
 */
@Repository
public interface UserMapper {

    UserDO getById(String openid);

    int insert(UserDO userDO);

}
