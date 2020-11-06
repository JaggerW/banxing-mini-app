package com.nju.banxing.demo.controller;

import com.alibaba.fastjson.JSON;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.common.sms.LoginVerSmsTemplate;
import com.nju.banxing.demo.common.wx.WxMaSessionInfo;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.domain.TestDO;
import com.nju.banxing.demo.domain.UserDO;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.mw.redis.SmsRedisKeyPrefix;
import com.nju.banxing.demo.mw.redis.UserRedisKeyPrefix;
import com.nju.banxing.demo.request.TestRequest;
import com.nju.banxing.demo.request.UserRegisterRequest;
import com.nju.banxing.demo.service.AliyunService;
import com.nju.banxing.demo.service.RedisService;
import com.nju.banxing.demo.service.UserService;
import com.nju.banxing.demo.service.WeixinService;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.util.ValidatorUtil;
import com.nju.banxing.demo.vo.AliyunSmsVO;
import lombok.extern.slf4j.Slf4j;
import netscape.security.UserTarget;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Test;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.nju.banxing.demo.config.AppContantConfig.USER_TOKEN_PARAM_NAME;


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


    @PostMapping(value = "/test")
    @MethodLog("测试")
    public SingleResult<TestDO> test(@RequestBody TestRequest request, String token){
        TestDO testDO = new TestDO();
        testDO.setName(request.getName());
        testDO.setToken(token);
        testDO.setId(request.getId());
        testDO.setList(request.getList());
        return SingleResult.success(testDO);
    }

    /**
     * token 在header中，拦截器以做处理判断
     * @return
     */
    @GetMapping(value = "/check/token")
    @MethodLog("查询userToken是否有效")
    public SingleResult<Boolean> checkUserToken() {
        return SingleResult.success(true);
    }

    /**
     * 参数解析器会自动将UserDO参数注入，且已做判空处理
     * @param userDO
     * @return
     */
    @GetMapping(value = "/check/user")
    @MethodLog("查询该用户是否已注册")
    public SingleResult<UserDO> checkUser(UserDO userDO) {
        return SingleResult.success(userDO);
    }

    @GetMapping(value = "/login")
    @MethodLog("用户登录")
    public SingleResult<String> login(String code) {
        if (StringUtils.isEmpty(code)) {
            return SingleResult.error(CodeMsg.PARAM_ERROR.fillArgs("code不能为空"));
        }

        // 调用微信登录接口，获取微信登陆状态
        WxMaSessionInfo sessionInfo = weixinService.login(code);
        if (null == sessionInfo) {
            return SingleResult.error(CodeMsg.SERVER_ERROR);
        }

        // 生成token并缓存
        String userToken = UUIDUtil.getUserToken();
        addToken2Redis(userToken, sessionInfo);
        return SingleResult.success(userToken);
    }


    @MethodLog("获取验证码")
    @GetMapping(value = "/ver_code")
    public SingleResult<Boolean> sendVerCode(String openid, String mobile) {
        if (!ValidatorUtil.isMobile(mobile)) {
            return SingleResult.error(CodeMsg.PARAM_ERROR.fillArgs("手机号码格式错误"));
        }

        // 生成验证码并缓存
        String verCode = UUIDUtil.getVerCode();
        String redisKey = openid + mobile;
        redisService.set(SmsRedisKeyPrefix.verCode, redisKey, verCode);

        // 阿里云发送短信
        AliyunSmsVO aliyunSmsVO = new AliyunSmsVO();
        aliyunSmsVO.setPhoneNumber(mobile);
        aliyunSmsVO.setSignName(AppContantConfig.ALIYUN_LOGIN_VERIFICATION_SMS_SIGN_NAME);
        aliyunSmsVO.setTemplateCode(AppContantConfig.ALIYUN_LOGIN_VERIFICATION_SMS_TEMPLATE_CODE);

        LoginVerSmsTemplate template = new LoginVerSmsTemplate();
        template.setCode(verCode);
        aliyunSmsVO.setTemplateParam(JSON.toJSONString(template));
        aliyunService.sendSMS(aliyunSmsVO);

        return SingleResult.success(true);
    }

    @MethodLog("新用户注册")
    @PostMapping(value = "/register")
    public SingleResult<UserDO> register(String openid, @Validated @RequestBody UserRegisterRequest registerRequest) {
        // @RequestBody 与 @RequestParam 能否共用，即一个java对象和单独一个变量能否传过来，content-type是啥
        // @RequestBody 与 @Validated能否共用

        // 获取openid

        // 校验验证码

        // 删除redis缓存

        // 获取用户加密数据 (try-catch:sessionKey过期->重新登陆)

        // 插入数据库
        UserDO userDO = new UserDO();

        return SingleResult.success(userDO);
    }

    /**
     * 将token缓存到redis中
     *
     * @param token
     * @param sessionInfo
     */
    private void addToken2Redis(String token, WxMaSessionInfo sessionInfo) {
        // 删掉该用户以前的token
        if (redisService.exists(UserRedisKeyPrefix.userOpenId, sessionInfo.getOpenId())) {
            String oldToken = redisService.get(UserRedisKeyPrefix.userOpenId, sessionInfo.getOpenId(), String.class);
            redisService.delete(UserRedisKeyPrefix.userToken, oldToken);
        }

        // 插入新的token
        redisService.set(UserRedisKeyPrefix.userOpenId, sessionInfo.getOpenId(), token);
        redisService.set(UserRedisKeyPrefix.userToken, token, sessionInfo);
    }
}
