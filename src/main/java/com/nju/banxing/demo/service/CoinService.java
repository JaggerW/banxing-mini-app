package com.nju.banxing.demo.service;

import com.nju.banxing.demo.domain.CoinDO;
import com.nju.banxing.demo.domain.CoinLogDO;
import com.nju.banxing.demo.domain.mapper.CoinLogMapper;
import com.nju.banxing.demo.domain.mapper.CoinMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: jaggerw
 * @Description: 资金
 * @Date: 2020/12/1
 */
@Service
@Slf4j
public class CoinService {

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private CoinLogMapper coinLogMapper;


    public CoinDO selectByOpenid(String openid){
        return coinMapper.selectById(openid);
    }

    public int insert(String openid){
        CoinDO coinDO = new CoinDO();
        coinDO.setId(openid);
        coinDO.setCreator(openid);
        coinDO.setModifier(openid);
        return coinMapper.insert(coinDO);
    }

    public int update(CoinDO coinDO){
        return coinMapper.updateById(coinDO);
    }

    public int insertLog(CoinLogDO coinLogDO){
        return coinLogMapper.insert(coinLogDO);
    }

}
