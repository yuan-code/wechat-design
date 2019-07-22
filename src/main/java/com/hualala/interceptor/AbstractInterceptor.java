package com.hualala.interceptor;

import com.alibaba.fastjson.JSON;
import com.hualala.model.User;
import com.hualala.service.WXService;
import com.hualala.util.CacheUtils;
import com.hualala.util.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.hualala.common.WXConstant.COOKIE_ACCESS_TOKEN_NAME;
import static com.hualala.common.WXConstant.COOKIE_EXPIRE_SECONDS;

/**
 * @author YuanChong
 * @create 2019-07-21 20:49
 * @desc
 */
public abstract class AbstractInterceptor implements HandlerInterceptor {


    @Autowired
    protected WXService wxService;

    /**
     * 为web视图补充js-api数据
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = UserHolder.getUser();
        ModelMap modelMap = modelAndView.getModelMap();
        modelMap.addAttribute("user", user);
        if (modelAndView != null) {
            //有视图 freemarker请求
            //补充js-api数据
            String requestURL = request.getRequestURL().toString();
            String queryString = request.getQueryString();
            String url = StringUtils.isEmpty(queryString) ? requestURL : requestURL + "?" + queryString;
            wxService.jsApiSignature(modelAndView.getModelMap(), url);
        }
    }

    /**
     * 清除user 防内存泄露
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserHolder.clear();
    }


    /**
     * cookie认证
     *
     * @param request
     * @return
     */
    protected User cookieAuth(HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<String> token = Arrays.stream(request.getCookies()).filter(c -> Objects.equals(c.getName(), COOKIE_ACCESS_TOKEN_NAME)).map(Cookie::getValue).findFirst();

            if (token.isPresent()) {
                String jsonUser = CacheUtils.get(token.get());
                if (StringUtils.isNotEmpty(jsonUser)) {
                    User user = JSON.parseObject(jsonUser, User.class);
                    UserHolder.setUser(user);
                    CacheUtils.expire(token.get(), COOKIE_EXPIRE_SECONDS);
                    return user;
                }
                //TODO 如果走到这里 可能是token过期了 没想好要怎么做
            }
        }
        return null;
    }
}
