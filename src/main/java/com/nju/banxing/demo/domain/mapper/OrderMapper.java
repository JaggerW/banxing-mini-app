package com.nju.banxing.demo.domain.mapper;

import com.nju.banxing.demo.domain.OrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;

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

    List<String> getErrorOrderCode();

    List<Map<String, Object>> getAutoCommentOrderInfo();

    Integer getStatusByCode(@Param("orderCode") String orderCode);

    String getTutorIdByCode(@Param("orderCode") String orderCode);

    Map<String, Object> getTutorInfoByOrderCode(@Param("orderCode") String orderCode);

    String getUserIdByOrderCode(@Param("orderCode") String orderCode);

    Map<String, Integer> getStatusAndVersionByCode(@Param("orderCode") String orderCode);

    BigDecimal getTotalCostByCode(@Param("orderCode") String orderCode);

    Long getOrderCountByTutorIdAndProcessFlag(@Param("tutorId") String tutorId,
                                              @Param("processFlag") Boolean processFlag,
                                              @Param("orderStatus") Integer orderStatus,
                                              @Param("rowStatus") Integer rowStatus);


    Long getOrderCountByUserIdAndProcessFlag(@Param("userId") String userId,
                                             @Param("processFlag") Boolean processFlag,
                                             @Param("orderStatus") Integer orderStatus,
                                             @Param("rowStatus") Integer rowStatus);


    Long getCommentOrderCountByUserIdAndProcessFlag(@Param("userId") String userId,
                                                    @Param("processFlag") Boolean processFlag,
                                                    @Param("commentStatus") Integer commentStatus,
                                                    @Param("rowStatus") Integer rowStatus);


    Long getReplyOrderCountByUserIdAndProcessFlag(@Param("userId") String userId,
                                                  @Param("processFlag") Boolean processFlag,
                                                  @Param("tutorStatus") Integer tutorStatus,
                                                  @Param("rowStatus") Integer rowStatus);

    Long getScheduleCountByUserId(@Param("userId") String userId,
                                  @Param("processFlag") Boolean processFlag,
                                  @Param("orderStatus") Integer orderStatus,
                                  @Param("tutorStatus") Integer tutorStatus,
                                  @Param("rowStatus") Integer rowStatus);


    Long getScheduleCountByTutorId(@Param("tutorId") String tutorId,
                                   @Param("processFlag") Boolean processFlag,
                                   @Param("orderStatus") Integer orderStatus,
                                   @Param("tutorStatus") Integer tutorStatus,
                                   @Param("rowStatus") Integer rowStatus);


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
                                                                  @Param("rowStatus") Integer rowStatus,
                                                                  @Param("offset") Long offset,
                                                                  @Param("pageSize") Long pageSize);


    /**
     *
     * @param userId
     * @param processFlag true,未处理；false,已处理
     * @param orderStatus 是否已结束的状态分界线，100
     * @param offset
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> getOrderListByUserIdAndProcessFlag(@Param("userId") String userId,
                                                                 @Param("processFlag") Boolean processFlag,
                                                                 @Param("orderStatus") Integer orderStatus,
                                                                 @Param("rowStatus") Integer rowStatus,
                                                                 @Param("offset") Long offset,
                                                                 @Param("pageSize") Long pageSize);


    /**
     *
     * @param userId
     * @param processFlag true,未处理；false,已处理
     * @param commentStatus 是否已评价状态标志位：0
     * @param offset
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> getCommentOrderListByUserIdAndProcessFlag(@Param("userId") String userId,
                                                                        @Param("processFlag") Boolean processFlag,
                                                                        @Param("commentStatus") Integer commentStatus,
                                                                        @Param("rowStatus") Integer rowStatus,
                                                                        @Param("offset") Long offset,
                                                                        @Param("pageSize") Long pageSize);

    /**
     *
     * @param userId
     * @param processFlag true,未处理；false,已处理
     * @param tutorStatus 是否同意的导师状态标志位：1
     * @param offset
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> getReplyOrderListByUserIdAndProcessFlag(@Param("userId") String userId,
                                                                      @Param("processFlag") Boolean processFlag,
                                                                      @Param("tutorStatus") Integer tutorStatus,
                                                                      @Param("rowStatus") Integer rowStatus,
                                                                      @Param("offset") Long offset,
                                                                      @Param("pageSize") Long pageSize);


    /**
     *
     * @param userId
     * @param tutorStatus 已同意
     * @param offset
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> getScheduleListByUserId(@Param("userId") String userId,
                                                      @Param("processFlag") Boolean processFlag,
                                                      @Param("orderStatus") Integer orderStatus,
                                                      @Param("tutorStatus") Integer tutorStatus,
                                                      @Param("rowStatus") Integer rowStatus,
                                                      @Param("offset") Long offset,
                                                      @Param("pageSize") Long pageSize);

    /**
     *
     * @param tutorId
     * @param tutorStatus 已同意
     * @param offset
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> getScheduleListByTutorId(@Param("tutorId") String tutorId,
                                                       @Param("processFlag") Boolean processFlag,
                                                       @Param("orderStatus") Integer orderStatus,
                                                       @Param("tutorStatus") Integer tutorStatus,
                                                       @Param("rowStatus") Integer rowStatus,
                                                       @Param("offset") Long offset,
                                                       @Param("pageSize") Long pageSize);

    Map<String, Object> getReserveOrderDetailByOrderCodeAndTutorId(@Param("orderCode") String orderCode,
                                                                   @Param("tutorId") String tutorId,
                                                                   @Param("rowStatus") Integer rowStatus);

    Map<String, Object> getCommentOrderDetailByOrderCodeAndUserId(@Param("orderCode") String orderCode,
                                                                   @Param("userId") String userId,
                                                                   @Param("rowStatus") Integer rowStatus);

    Map<String, Object> getScheduleDetailByOrderCodeAndTutorId(@Param("orderCode") String orderCode,
                                                               @Param("tutorId") String tutorId,
                                                               @Param("rowStatus") Integer rowStatus);

    Map<String, Object> getReplyOrderDetailByOrderCodeAndUserId(@Param("orderCode") String orderCode,
                                                                @Param("userId") String userId,
                                                                @Param("rowStatus") Integer rowStatus);

    Map<String, Object> getOrderConferenceInfoByOrderCode(@Param("orderCode") String orderCode,
                                                          @Param("rowStatus") Integer rowStatus);

}
