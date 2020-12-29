package com.nju.banxing.demo.domain.mapper;

import com.nju.banxing.demo.domain.OrderLogDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单流水表 Mapper 接口
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-18
 */
public interface OrderLogMapper extends BaseMapper<OrderLogDO> {

    List<Integer> getOrderStatusByOrderCode(@Param("orderCode") String orderCode,
                                            @Param("rowStatus") Integer rowStatus);

}
