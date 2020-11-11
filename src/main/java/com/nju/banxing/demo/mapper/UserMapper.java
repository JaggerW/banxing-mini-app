package com.nju.banxing.demo.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nju.banxing.demo.domain.UserDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


/**
 * @Author: jaggerw
 * @Description: 用户数据库层接口
 * @Date: 2020/11/5
 */
public interface UserMapper extends BaseMapper<UserDO> {

    @Update({
            "update user set login_count = login_count + 1 ${ew.customSqlSegment}"
    })
    int updateUserLog(@Param(Constants.WRAPPER)Wrapper wrapper);

}
