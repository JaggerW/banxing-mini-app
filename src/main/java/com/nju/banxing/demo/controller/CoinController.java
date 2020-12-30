package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.domain.CoinDO;
import com.nju.banxing.demo.service.CoinService;
import com.nju.banxing.demo.vo.CoinVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: jaggerw
 * @Description: 钱包
 * @Date: 2020/12/30
 */
@RestController
@RequestMapping("/coin")
@Slf4j
public class CoinController {

    @Autowired
    private CoinService coinService;

    // 钱包页面
    @GetMapping("/info")
    @MethodLog("获取钱包金额信息")
    public SingleResult<CoinVO> getCoin(String openid){
        CoinDO coinDO = coinService.selectByOpenid(openid);
        if(null == coinDO){
            coinService.insert(openid);
            coinDO = coinService.selectByOpenid(openid);
        }

        CoinVO vo = new CoinVO();
        vo.setAvailableAmount(coinDO.getAvailableAmount());
        vo.setOccupyAmount(coinDO.getOccupyAmount());

        return SingleResult.success(vo);
    }

    // TODO 提现 企业付款

}
