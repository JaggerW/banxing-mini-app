package com.nju.banxing.demo.domain.mapper;

import com.nju.banxing.demo.domain.OrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-18
 */
public interface OrderMapper extends BaseMapper<OrderDO> {

    Integer getStatusByIdAndCode(@Param("openid") String openid, @Param("orderCode") String orderCode);


}
