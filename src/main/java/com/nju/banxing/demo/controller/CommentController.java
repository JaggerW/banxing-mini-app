package com.nju.banxing.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.domain.CommentDO;
import com.nju.banxing.demo.enums.CommentStatusEnum;
import com.nju.banxing.demo.enums.ConsultationTypeEnum;
import com.nju.banxing.demo.request.CommentListQuery;
import com.nju.banxing.demo.request.CommentRequest;
import com.nju.banxing.demo.service.*;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.vo.CoinVO;
import com.nju.banxing.demo.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @Author: jaggerw
 * @Description: 评论
 * @Date: 2020/12/24
 */
@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;


    @GetMapping("/list")
    @MethodLog("获取评论列表")
    public PagedResult<CommentVO> getComment(CommentListQuery query){
        Integer type = query.getConsultationType();
        if(ObjectUtils.isEmpty(type)
                || (ObjectUtils.isNotEmpty(type)
                    && !ConsultationTypeEnum.KAO_YAN.getCode().equals(type)
                    && !ConsultationTypeEnum.BAO_YAN.getCode().equals(type))){
            type = ConsultationTypeEnum.KAO_YAN.getCode();
        }

        IPage<CommentDO> page = commentService.getAll(type, query.getTutorId(), query.getPageIndex(), query.getPageSize());
        List<CommentVO> list = page.getRecords().stream().map(commentDO -> {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(commentDO, commentVO);
            Map<String, Object> map = userService.getNickNameAndAvaById(commentDO.getUserId());
            commentVO.setNickName((String) map.get("nickName"));
            commentVO.setUserAvatarUrl((String) map.get("avatarUrl"));
            commentVO.setCommentTimeStamp(DateUtil.toTimeStamp(commentDO.getCommentTime()));
            return commentVO;
        }).collect(Collectors.toList());

        return PagedResult.success(list,page.getCurrent(),page.getSize(),page.getTotal(),page.getPages());
    }


    @PostMapping("/add")
    @MethodLog("发表新评论")
    @Transactional
    public SingleResult<Boolean> comment(String openid,
                                         @Validated @RequestBody CommentRequest request){

        // TODO 关于score范围的校验

        CommentDO commentDO = buildDO(request, openid);
        boolean b = commentService.publishNewComment(commentDO, CommentStatusEnum.COMMENTED.getCode());
        return SingleResult.success(b);
    }

    private CommentDO buildDO(CommentRequest request, String openid){
        CommentDO commentDO = new CommentDO();
        commentDO.setCommentContent(request.getContent());
        commentDO.setCommentScore(request.getScore());
        commentDO.setCommentTime(DateUtil.now());
        commentDO.setOrderCode(request.getOrderCode());
        commentDO.setId(UUIDUtil.getCommentCode());
        commentDO.setCreator(openid);
        commentDO.setModifier(openid);
        commentDO.setUserId(openid);

        Map<String, Object> map = orderService.getTutorInfoByOrderCode(request.getOrderCode());
        String tutorId = (String) map.get("tutorId");
        commentDO.setConsultationType((Integer) map.get("consultationType"));
        commentDO.setTutorId(tutorId);

        return commentDO;
    }

}
