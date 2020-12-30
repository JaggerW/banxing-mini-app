package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.domain.CommentDO;
import com.nju.banxing.demo.request.CommentRequest;
import com.nju.banxing.demo.service.CommentService;
import com.nju.banxing.demo.service.OrderService;
import com.nju.banxing.demo.service.TutorService;
import com.nju.banxing.demo.util.DateUtil;
import com.nju.banxing.demo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


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
    private TutorService tutorService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    @MethodLog("发表新评论")
    @Transactional
    public SingleResult<Boolean> comment(String openid,
                                         @Validated @RequestBody CommentRequest request){

        // TODO 关于score范围的校验

        CommentDO commentDO = buildDO(request, openid);

        commentService.insert(commentDO);
        tutorService.updateCommentScore(commentDO.getTutorId(),request.getScore());

        return SingleResult.success(true);
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
