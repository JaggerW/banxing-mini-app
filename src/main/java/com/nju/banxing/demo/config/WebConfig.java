package com.nju.banxing.demo.config;

import com.nju.banxing.demo.aspect.SecurityInterceptor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Author: jaggerw
 * @Description: webMvc配置类
 * @Date: 2020/11/6
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Autowired
    private UserTokenArgumentResolver userTokenArgumentResolver;

    @Autowired
    private UserDOArgumentResolver userDOArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userTokenArgumentResolver);
        resolvers.add(userDOArgumentResolver);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/banxing",
                pd -> pd.isAnnotationPresent(RestController.class) || pd.isAnnotationPresent(Controller.class));
    }
}
