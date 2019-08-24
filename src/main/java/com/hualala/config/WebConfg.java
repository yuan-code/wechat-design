package com.hualala.config;

import com.hualala.componet.UserArgumentResolver;
import com.hualala.interceptor.WebAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author YuanChong
 * @create 2019-07-09 20:08
 * @desc
 */
@Configuration
public class WebConfg implements WebMvcConfigurer {

    /**
     * 微信js授权拦截器
     */
    @Autowired
    private WebAuthInterceptor webAuthInterceptor;

    /**
     * 参数解析器
     */
    @Autowired
    private UserArgumentResolver userArgumentResolver;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webAuthInterceptor).excludePathPatterns("/wx/**").addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }
}
