package com.nju.banxing.demo.config;

import com.nju.banxing.demo.aspect.AdminInterceptor;
import com.nju.banxing.demo.aspect.TokenInterceptor;
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
    private TokenInterceptor tokenInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Autowired
    private UserTokenArgumentResolver userTokenArgumentResolver;

    @Autowired
    private UserDOArgumentResolver userDOArgumentResolver;

    @Autowired
    private OpenIdArgumentResolver openIdArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/banxing/**");
        registry.addInterceptor(adminInterceptor).addPathPatterns("/banxing/admin/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userTokenArgumentResolver);
        resolvers.add(userDOArgumentResolver);
        resolvers.add(openIdArgumentResolver);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/banxing",
                pd -> pd.isAnnotationPresent(RestController.class) || pd.isAnnotationPresent(Controller.class));
    }
}
