package com.nju.banxing.demo.domain.mapper;

import com.nju.banxing.demo.domain.TutorDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 导师表 Mapper 接口
 * </p>
 *
 * @author JaggerW
 * @since 2020-11-11
 */
public interface TutorMapper extends BaseMapper<TutorDO> {

    Map<String, Object> getApplyInfoById(@Param("openid") String tutorId);

    Integer getStatusById(@Param("openid") String tutorId);

    String getWorkTimeById(@Param("openid") String openid);

    BigDecimal getConsultationCost(@Param("openid") String openid);
}
