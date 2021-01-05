package com.nju.banxing.demo.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nju.banxing.demo.annotation.Retry;
import com.nju.banxing.demo.config.WxMaConfig;
import com.nju.banxing.demo.domain.CoinDO;
import com.nju.banxing.demo.domain.CoinLogDO;
import com.nju.banxing.demo.domain.mapper.CoinLogMapper;
import com.nju.banxing.demo.domain.mapper.CoinMapper;
import com.nju.banxing.demo.enums.CoinProcessTypeEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.exception.RetryException;
import com.nju.banxing.demo.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

    @Autowired
    private WxMaConfig wxMaConfig;


    public CoinDO selectByOpenid(String openid){
        return coinMapper.selectById(openid);
    }

    public boolean insert(String openid){
        CoinDO coinDO = new CoinDO();
        coinDO.setId(openid);
        coinDO.setCreator(openid);
        coinDO.setModifier(openid);
        return coinMapper.insert(coinDO) > 0;
    }


    public boolean enableCoin(String userId, String tutorId, BigDecimal money, String orderCode){
        CoinDO coinDO = coinMapper.selectById(tutorId);
        if (coinDO.getOccupyAmount().compareTo(money) < 0){
            throw new GlobalException(CodeMsg.OCCUPY_ERROR);
        }
        coinDO.setOccupyAmount(coinDO.getOccupyAmount().subtract(money));
        coinDO.setAvailableAmount(coinDO.getAvailableAmount().add(money));
        coinDO.setModifier(userId);
        coinDO.setModifyTime(DateUtil.now());
        boolean update = coinMapper.updateById(coinDO) > 0;

        CoinLogDO coinLogDO = new CoinLogDO();
        coinLogDO.setOrderCode(orderCode);
        coinLogDO.setCoinAmount(money);
        coinLogDO.setTargetId(tutorId);
        coinLogDO.setSourceId(tutorId);
        coinLogDO.setMerchantCode(wxMaConfig.getMchid());
        coinLogDO.setProcessType(CoinProcessTypeEnum.ENABLE.getCode());
        coinLogDO.setModifier(userId);
        coinLogDO.setCreator(userId);
        boolean insertLog = insertLog(coinLogDO);

        if(!update || !insertLog){
            throw new RetryException(CodeMsg.RETRY_ON_FAIL);
        }

        return true;
    }

    public boolean update(CoinDO coinDO){
        return coinMapper.updateById(coinDO) > 0;
    }

    public boolean insertLog(CoinLogDO coinLogDO){
        return coinLogMapper.insert(coinLogDO) > 0;
    }

}
