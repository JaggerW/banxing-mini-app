package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TestController {

    @Autowired
    private UserService userService;

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

}
