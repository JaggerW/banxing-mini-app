package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.wx.WxMaSessionInfo;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.mw.redis.UserRedisKeyPrefix;
import com.nju.banxing.demo.service.RedisService;
import com.nju.banxing.demo.service.WeixinService;
import com.nju.banxing.demo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import netscape.security.UserTarget;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: jaggerw
 * @Description: 用户模块controller
 * @Date: 2020/11/5
 */
@RestController
@RequestMapping(value = "/user")
@Slf4j
public class UserController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private WeixinService weixinService;

    @GetMapping(value = "/check/token")
    @MethodLog("查询userToken是否有效")
    public SingleResult<Boolean> checkUserToken(@RequestParam(value = AppContantConfig.USER_TOKEN_PARAM_NAME) String token){
        try {
            if(!StringUtils.isEmpty(token) && redisService.exists(UserRedisKeyPrefix.userToken,token)){
                redisService.updateExpire(UserRedisKeyPrefix.userToken,token);
                return SingleResult.success(true);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return SingleResult.success(false);
    }

    @GetMapping(value = "/login")
    @MethodLog("用户登录")
    public SingleResult<String> login(@RequestParam(value = "code") String code){
        if(StringUtils.isEmpty(code)){
            return SingleResult.error(CodeMsg.PARAM_ERROR.fillArgs("code不能为空"));
        }

        // 调用微信登录接口，获取微信登陆状态
        WxMaSessionInfo sessionInfo = weixinService.login(code);
        if(null == sessionInfo){
            return SingleResult.error(CodeMsg.SERVER_ERROR);
        }

        // 生成token并缓存
        String userToken = UUIDUtil.getUserToken();
        addToken2Redis(userToken,sessionInfo);
        return SingleResult.success(userToken);
    }

    /**
     * 将token缓存到redis中
     * @param token
     * @param sessionInfo
     */
    private void addToken2Redis(String token, WxMaSessionInfo sessionInfo){
        // 删掉该用户以前的token
        if(redisService.exists(UserRedisKeyPrefix.userOpenId,sessionInfo.getOpenId())){
            String oldToken = redisService.get(UserRedisKeyPrefix.userOpenId,sessionInfo.getOpenId(),String.class);
            redisService.delete(UserRedisKeyPrefix.userToken,oldToken);
        }

        // 插入新的token
        redisService.set(UserRedisKeyPrefix.userOpenId,sessionInfo.getOpenId(),token);
        redisService.set(UserRedisKeyPrefix.userToken,token,sessionInfo);
    }
}
