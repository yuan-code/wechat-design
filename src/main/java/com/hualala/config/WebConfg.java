package com.hualala.config;

import com.hualala.componet.UserArgumentResolver;
import com.hualala.interceptor.PassportInterceptor;
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
     * 微信js授权拦截器 路径定义规则：controller内 /auth开头接口
     */
    @Autowired
    private WebAuthInterceptor webAuthInterceptor;

    @Autowired
    private UserArgumentResolver userArgumentResolver;

    /**
     * 授权后的内部页面拦截器 路径定义规则：controller内 /passport开头接口
     */
    @Autowired
    private PassportInterceptor passportInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webAuthInterceptor)
                .addPathPatterns("/article/auth/**");
        registry.addInterceptor(passportInterceptor)
                .addPathPatterns("/article/passport/**")
                .addPathPatterns("/user/passport/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }
}
