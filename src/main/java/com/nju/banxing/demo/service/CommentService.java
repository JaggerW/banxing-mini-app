package com.nju.banxing.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nju.banxing.demo.domain.CommentDO;
import com.nju.banxing.demo.domain.UserDO;
import com.nju.banxing.demo.domain.mapper.CommentMapper;
import com.nju.banxing.demo.domain.mapper.UserMapper;
import com.nju.banxing.demo.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: jaggerw
 * @Description: 评论
 * @Date: 2020/11/14
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    public IPage<CommentDO> getAll(Integer type, String tutorId, Long pageIndex, Long pageSize){
        Page<CommentDO> page = new Page<>(pageIndex,pageSize);
        return commentMapper.selectPage(page, new QueryWrapper<CommentDO>().lambda()
                .eq(CommentDO::getId, tutorId)
                .eq(CommentDO::getConsultationType, type));
    }
}
