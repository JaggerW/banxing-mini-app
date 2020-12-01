package com.nju.banxing.demo.domain.mapper;

import com.nju.banxing.demo.domain.OrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-18
 */
public interface OrderMapper extends BaseMapper<OrderDO> {

    Integer getStatusByCode(@Param("orderCode") String orderCode);

    String getTutorIdByCode(@Param("orderCode") String orderCode);

    Map<String, Integer> getStatusAndVersionByCode(@Param("orderCode") String orderCode);

    BigDecimal getTotalCostByCode(@Param("orderCode") String orderCode);
}
