package com.nju.banxing.demo.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nju.banxing.demo.domain.ReadDO;
import com.nju.banxing.demo.domain.mapper.ReadMapper;
import com.nju.banxing.demo.enums.CommentStatusEnum;
import com.nju.banxing.demo.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author: jaggerw
 * @Description: 用户已读时间
 * @Date: 2020/12/8
 */
@Service
public class ReadService {

    @Autowired
    private ReadMapper readMapper;

    public boolean insert(ReadDO readDO){
        return readMapper.insert(readDO) > 0;
    }

    public boolean updateOrderApplyTimeByTutorId(String tutorId){
        return readMapper.update(null,
                new UpdateWrapper<ReadDO>().lambda()
                        .eq(ReadDO::getId,tutorId)
                        .set(ReadDO::getOrderApply, DateUtil.now())) > 0;
    }

    public boolean updateOrderReplyTimeByUserId(String userId){
        return readMapper.update(null,
                new UpdateWrapper<ReadDO>().lambda()
                        .eq(ReadDO::getId,userId)
                        .set(ReadDO::getOrderReply, DateUtil.now())) > 0;
    }

    public boolean updateOrderCommentTimeByUserId(String userId){
        return readMapper.update(null,
                new UpdateWrapper<ReadDO>().lambda()
                        .eq(ReadDO::getId,userId)
                        .set(ReadDO::getOrderComment, DateUtil.now())) > 0;
    }

    public long getCountOfNewOrderApplyById(String userId){
        return readMapper.getCountOfNewOrderApplyById(userId);
    }

    public long getCountOfNewOrderReplyById(String userId){
        return readMapper.getCountOfNewOrderReplyById(userId);
    }

    public long getCountOfNewOrderCommentById(String userId){
        return readMapper.getCountOfNewOrderCommentById(userId, CommentStatusEnum.TO_COMMENT.getCode());
    }

    public boolean delete(String openid){
        return readMapper.deleteById(openid) > 0;
    }

}
