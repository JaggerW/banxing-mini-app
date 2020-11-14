package com.nju.banxing.demo.aspect;

import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: jaggerw
 * @Description: 拦截器，验证token和权限（待定）
 * @Date: 2020/11/6
 */
@Service
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO for dev
        return true;


//        String path = request.getServletPath();
//        if (ignore(path)) {
//            return true;
//        }
//
//        String token = request.getHeader(AppContantConfig.USER_TOKEN_HEADER_NAME);
//        if(userService.existToken(token)){
//            log.debug(token);
//            return true;
//        }else {
//            throw new GlobalException(CodeMsg.NULL_TOKEN);
//        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private boolean ignore(String path) {
        for (String ignorePath :
                AppContantConfig.IGNORE_TOKEN_PATH) {
            if (path.endsWith(ignorePath)) {
                return true;
            }
        }
        return false;
    }
}
