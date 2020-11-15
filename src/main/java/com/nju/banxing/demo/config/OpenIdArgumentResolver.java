package com.nju.banxing.demo.config;

import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: jaggerw
 * @Description: openid参数解析
 * @Date: 2020/11/6
 */
@Service
public class OpenIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        String parameterName = methodParameter.getParameterName();
        return AppContantConfig.OPEN_ID_PARAM_NAME.equals(parameterName);
    }

    // TODO openid没有注入
    // TODO locattime时间格式化

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        String token = request.getHeader(AppContantConfig.USER_TOKEN_HEADER_NAME);
        String openid = userService.getOpenidByToken(token);
        if(StringUtils.isEmpty(openid)){
            throw new GlobalException(CodeMsg.NULL_TOKEN);
        }
        return openid;
    }
}
