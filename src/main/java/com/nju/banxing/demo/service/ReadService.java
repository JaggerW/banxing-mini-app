package com.nju.banxing.demo.service;

import com.nju.banxing.demo.domain.ReadDO;
import com.nju.banxing.demo.domain.mapper.ReadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: jaggerw
 * @Description: 用户已读时间
 * @Date: 2020/12/8
 */
@Service
public class ReadService {

    @Autowired
    private ReadMapper readMapper;

    public boolean insert(ReadDO readDO){
        return readMapper.insert(readDO) > 0;
    }

    public boolean delete(String openid){
        return readMapper.deleteById(openid) > 0;
    }

}
