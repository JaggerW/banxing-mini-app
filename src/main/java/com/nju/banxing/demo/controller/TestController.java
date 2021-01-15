package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.annotation.Retry;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.domain.ReadDO;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.exception.RetryException;
import com.nju.banxing.demo.service.ReadService;
import com.nju.banxing.demo.service.TutorService;
import com.nju.banxing.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: jaggerw
 * @Description: 测试
 * @Date: 2020/12/9
 */

@RequestMapping("/test")
@RestController
@Slf4j
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReadService readService;

    @Autowired
    private TutorService tutorService;

    @PostMapping("/delete_user")
    @MethodLog("删除用户")
    public SingleResult<Boolean> deleteUser(String openid){
        boolean b = userService.deleteUser(openid);
        return SingleResult.success(b);
    }

    @PostMapping("delete_tutor")
    @MethodLog("删除导师")
    public SingleResult<Boolean> deleteTutor(String openid){
        boolean b = tutorService.delete(openid);
        return SingleResult.success(b);
    }

    @PostMapping("handle_tutor")
    @MethodLog("处理导师请求")
    public SingleResult<Boolean> handleTutor(String openid, Integer status){
        boolean b = tutorService.handleTutor(openid, status);
        return SingleResult.success(b);
    }

    @PostMapping("/test")
    @MethodLog("测试")
    public SingleResult<Boolean> test(){
        int i = 0;
        System.out.println(1/i);
        return SingleResult.success(true);
    }

    @GetMapping("/retry_test")
    @MethodLog("测试RETRY")
    @Retry
    public SingleResult<Boolean> testRetry(){
        log.info("===== 进入retryController =====");
        try {
            throw new RetryException(CodeMsg.RETRY_ON_FAIL);
        } catch (RetryException e){
            throw e;
        } catch (GlobalException e){
            log.info("已再controller中捕获异常");
        }
        return null;
    }

    @GetMapping("/trans_test")
    @MethodLog("测试事务")
    @Transactional
    public SingleResult<Boolean> testTransaction(){
        log.info("===== testTransaction =====");
        ReadDO readDO = new ReadDO();
        readDO.setId("test_user_002");
        boolean insert = readService.insert(readDO);
        System.out.println(insert);
        boolean other = readService.insert(readDO);
        System.out.println(other);
        return SingleResult.success(true);
    }

}
