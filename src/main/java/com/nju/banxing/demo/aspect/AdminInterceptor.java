package com.nju.banxing.demo.aspect;

import com.nju.banxing.demo.annotation.Retry;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.service.AdminService;
import com.nju.banxing.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: jaggerw
 * @Description: 管理员拦截器
 * @Date: 2021/1/18
 */
@Slf4j
@Service
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 管理员鉴权
        String path = request.getServletPath();
        log.info("===== Servlet Path : {} =====",path);

        String token = request.getHeader(AppContantConfig.USER_TOKEN_HEADER_NAME);
        String openid = userService.getOpenidByToken(token);
        return adminService.isAdmin(openid);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
