package com.nju.banxing.demo.domain.mapper;

import com.nju.banxing.demo.domain.CommentDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 导师评价表 Mapper 接口
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-14
 */
public interface CommentMapper extends BaseMapper<CommentDO> {

    Map<String, Object> getCommentInfoByOrderCode(@Param("orderCode") String orderCode);
}
