package com.nju.banxing.demo.controller;

import com.alibaba.fastjson.JSON;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.sms.LoginVerSmsTemplate;
import com.nju.banxing.demo.common.wx.WxSessionInfo;
import com.nju.banxing.demo.common.wx.WxUserInfo;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.mw.redis.SmsRedisKeyPrefix;
import com.nju.banxing.demo.mw.redis.UserRedisKeyPrefix;
import com.nju.banxing.demo.request.UserRegisterRequest;
import com.nju.banxing.demo.service.*;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.util.ValidatorUtil;
import com.nju.banxing.demo.vo.AliyunSmsVO;
import com.nju.banxing.demo.vo.UnreadVO;
import com.nju.banxing.demo.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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

    @Autowired
    private UserService userService;

    @Autowired
    private AliyunService aliyunService;

    @Autowired
    private ReadService readService;


    @PostMapping(value = "/post_test")
    @MethodLog("测试")
    public SingleResult<String> testPost() {
        return SingleResult.success("hello world");
    }

    @GetMapping(value = "/get_test")
    @MethodLog("测试")
    public SingleResult<String> testGet() {
        return SingleResult.success("hello world");
    }

    /**
     * token 在header中，拦截器以做处理判断
     *
     * @return
     */
    @GetMapping(value = "/check_token")
    @MethodLog("查询userToken是否有效")
    public SingleResult<Boolean> checkUserToken() {
        return SingleResult.success(true);
    }

    /**
     * @param openid
     * @return
     */
    @GetMapping(value = "/check_user")
    @MethodLog("查询该用户是否已注册")
    public SingleResult<Boolean> checkUser(String openid) {
        return SingleResult.success(userService.existUser(openid));
    }

    @GetMapping(value = "/login")
    @MethodLog("用户登录")
    public SingleResult<String> login(String code) {
        if (StringUtils.isEmpty(code)) {
            return SingleResult.error(CodeMsg.BIND_ERROR.fillArgs("code不能为空"));
        }

        // 调用微信登录接口，获取微信登陆状态
        WxSessionInfo sessionInfo = weixinService.login(code);
        if (null == sessionInfo) {
            return SingleResult.error(CodeMsg.SERVER_ERROR);
        }

        // 生成token并缓存
        String userToken = UUIDUtil.getUserToken();
        addToken2Redis(userToken, sessionInfo);

        // 若存在用户，则更新登录次数和最近登录时间
        String openid = sessionInfo.getOpenId();
        if(userService.existUser(openid)){
            userService.updateUserLog(openid);
        }

        return SingleResult.success(userToken);
    }

    @MethodLog("获取验证码")
    @GetMapping(value = "/get_ver_code")
    public SingleResult<Boolean> sendVerCode(String openid, String mobile) {
        if (!ValidatorUtil.isMobile(mobile)) {
            return SingleResult.error(CodeMsg.BIND_ERROR.fillArgs("手机号码格式错误"));
        }

        // 生成验证码并缓存
        String verCode = UUIDUtil.getVerCode();
        String redisKey = openid + mobile;
        redisService.set(SmsRedisKeyPrefix.verCode, redisKey, verCode);

        // 阿里云发送短信
        AliyunSmsVO aliyunSmsVO = new AliyunSmsVO();
        aliyunSmsVO.setPhoneNumber(mobile);
        aliyunSmsVO.setSignName(AppContantConfig.ALIYUN_SMS_SIGN_NAME);
        aliyunSmsVO.setTemplateCode(AppContantConfig.ALIYUN_SMS_LOGIN_VERIFICATION_TEMPLATE_CODE);

        LoginVerSmsTemplate template = new LoginVerSmsTemplate();
        template.setCode(verCode);
        aliyunSmsVO.setTemplateParam(JSON.toJSONString(template));
        aliyunService.sendSMS(aliyunSmsVO);

        return SingleResult.success(true);
    }

    @MethodLog("新用户注册")
    @PostMapping(value = "/register")
    public SingleResult<Boolean> register(String token, @Validated @RequestBody UserRegisterRequest registerRequest) {

        // 获取登录态
        WxSessionInfo sessionInfo = redisService.get(UserRedisKeyPrefix.userToken, token, WxSessionInfo.class);
        String openid = sessionInfo.getOpenId();
        String sessionKey = sessionInfo.getSessionKey();

        // 校验验证码
        if (!checkVerCode(openid, registerRequest.getMobile(), registerRequest.getVerCode())) {
            return SingleResult.error(CodeMsg.ERROR_VER_CODE);
        }

        Boolean existUser = userService.existUser(openid);
        if(existUser){
            return SingleResult.error(CodeMsg.DUP_USER);
        }

        // 获取用户加密数据 (try-catch:sessionKey过期->重新登陆)
        WxUserInfo userInfo = weixinService.getUserInfo(sessionKey, registerRequest.getSignature(),
                registerRequest.getRawData(), registerRequest.getEncryptedData(), registerRequest.getIv());

        // 插入数据库
        boolean flag = userService.insertUser(openid, registerRequest, userInfo);
        return flag ? SingleResult.success(true) : SingleResult.error(CodeMsg.FAIL_REGISTER);
    }

    @MethodLog("获取当前用户信息")
    @GetMapping("/get_info")
    public SingleResult<UserInfoVO> getUserInfo(String openid){
        UserInfoVO userInfo = userService.getUserInfo(openid);
        if(ObjectUtils.isEmpty(userInfo)){
            return SingleResult.error(CodeMsg.NULL_USER);
        }else{
            return SingleResult.success(userInfo);
        }
    }

    @GetMapping("/get_unread")
    @MethodLog("获取各模块未读消息个数")
    public SingleResult<UnreadVO> getUnreadInfo(String openid){
        UnreadVO unreadVO = new UnreadVO();
        long applyById = readService.getCountOfNewOrderApplyById(openid);
        long commentById = readService.getCountOfNewOrderCommentById(openid);
        long replyById = readService.getCountOfNewOrderReplyById(openid);
        unreadVO.setOrderApply(applyById);
        unreadVO.setOrderReply(replyById);
        unreadVO.setOrderComment(commentById);
        return SingleResult.success(unreadVO);
    }

    @PostMapping("/update_info")
    @MethodLog("更新用户信息")
    public SingleResult<Boolean> updateUserInfo(String openid, @Validated @RequestBody UserRegisterRequest registerRequest){

        // 校验验证码
        if(registerRequest.getMobileFlag()){
            if (!checkVerCode(openid, registerRequest.getMobile(), registerRequest.getVerCode())) {
                return SingleResult.error(CodeMsg.ERROR_VER_CODE);
            }
        }


        userService.updateUserInfo(openid, registerRequest);
        return SingleResult.success(true);
    }


    /**
     * 将token缓存到redis中
     *
     * @param token
     * @param sessionInfo
     */
    private void addToken2Redis(String token, WxSessionInfo sessionInfo) {
        // 删掉该用户以前的token
        if (redisService.exists(UserRedisKeyPrefix.userOpenId, sessionInfo.getOpenId())) {
            String oldToken = redisService.get(UserRedisKeyPrefix.userOpenId, sessionInfo.getOpenId(), String.class);
            redisService.delete(UserRedisKeyPrefix.userToken, oldToken);
        }

        // 插入新的token
        redisService.set(UserRedisKeyPrefix.userOpenId, sessionInfo.getOpenId(), token);
        redisService.set(UserRedisKeyPrefix.userToken, token, sessionInfo);
    }

    private boolean checkVerCode(String openid, String mobile, String verCode) {
        String redisKey = openid + mobile;
        String redisVerCode = redisService.get(SmsRedisKeyPrefix.verCode, redisKey, String.class);
        if (verCode.equals(redisVerCode)) {
            return redisService.delete(SmsRedisKeyPrefix.verCode, redisKey);
        } else {
            return false;
        }
    }
}
