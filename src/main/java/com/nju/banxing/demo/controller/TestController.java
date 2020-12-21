package com.nju.banxing.demo.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.annotation.Retry;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.domain.ReadDO;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.exception.RetryException;
import com.nju.banxing.demo.service.ReadService;
import com.nju.banxing.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/delete_user")
    @MethodLog("删除用户")
    public SingleResult<Boolean> deleteUser(String openid){
        boolean b = userService.deleteUser(openid);
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
    public SingleResult<Boolean> testTransaction(){
        log.info("===== testTransaction =====");
        ReadDO readDO = new ReadDO();
        readDO.setId("testUser_001");
        ReadDO updateReadDO = new ReadDO();
        updateReadDO.setId("testUser_001");
        updateReadDO.setExtension(JSON.toJSONString(readDO));

        return SingleResult.success(true);
    }

}
