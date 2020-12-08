package com.nju.banxing.demo.service;

import com.nju.banxing.demo.domain.ReadDO;
import com.nju.banxing.demo.domain.mapper.ReadMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: jaggerw
 * @Description: 用户已读时间
 * @Date: 2020/12/8
 */
public class ReadService {

    @Autowired
    private ReadMapper readMapper;

    public int insert(ReadDO readDO){
        return readMapper.insert(readDO);
    }

}
