package com.nju.banxing.demo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: jaggerw
 * @Description: 金额
 * @Date: 2020/12/30
 */
@Data
public class CoinVO implements Serializable {
    private static final long serialVersionUID = 2380796930589161115L;


    /**
     * 当前可用余额
     */
    private BigDecimal availableAmount;

    /**
     * 当前已占用余额，尚未入账
     */
    private BigDecimal occupyAmount;
}
