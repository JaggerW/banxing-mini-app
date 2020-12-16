package com.nju.banxing.demo.domain.mapper;

import com.nju.banxing.demo.domain.OrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
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

    Long getOrderCountByTutorIdAndProcessFlag(@Param("tutorId") String tutorId,
                                              @Param("processFlag") Boolean processFlag,
                                              @Param("orderStatus") Integer orderStatus);

    /**
     *
     * @param tutorId
     * @param processFlag true,未处理；false,已处理
     * @param orderStatus 是否已处理的状态分界线，30
     * @param offset
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> getOrderListByTutorIdAndProcessFlag(@Param("tutorId") String tutorId,
                                                                  @Param("processFlag") Boolean processFlag,
                                                                  @Param("orderStatus") Integer orderStatus,
                                                                  @Param("offset") Long offset,
                                                                  @Param("pageSize") Long pageSize);

    Map<String, Object> getOrderDetailByOrderCodeAndTutorId(@Param("orderCode") String orderCode,
                                                            @Param("tutorId") String tutorId);

}
