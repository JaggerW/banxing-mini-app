package com.nju.banxing.demo.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nju.banxing.demo.domain.ReadDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户已读时间表 Mapper 接口
 * </p>
 *
 * @author JaggerW
 * @since 2020-12-08
 */
public interface ReadMapper extends BaseMapper<ReadDO> {

    long getCountOfNewOrderApplyById(@Param("openid") String userId);

    long getCountOfNewOrderReplyById(@Param("openid") String userId);

    long getCountOfNewOrderCommentById(@Param("openid") String userId,
                                      @Param("status")Integer status);
}
