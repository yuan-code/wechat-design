package com.hualala.interceptor;

import com.alibaba.fastjson.JSON;
import com.hualala.common.ResultCode;
import com.hualala.model.User;
import com.hualala.service.WXService;
import com.hualala.util.CacheUtils;
import com.hualala.util.ResultUtils;
import com.hualala.util.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.hualala.common.WXConstant.COOKIE_ACCESS_TOKEN_NAME;
import static com.hualala.common.WXConstant.COOKIE_EXPIRE_SECONDS;


/**
 * @author YuanChong
 * @create 2019-07-09 18:44
 * @desc 授权后的内部页面拦截器
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private WXService wxService;


    /**
     * 微信JS授权统一处理
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getCookies() != null) {
            Optional<String> token = Arrays.stream(request.getCookies()).filter(c -> Objects.equals(c.getName(), COOKIE_ACCESS_TOKEN_NAME)).map(Cookie::getValue).findFirst();

            if (token.isPresent()) {
                String jsonUser = CacheUtils.get(token.get());
                if (StringUtils.isNotEmpty(jsonUser)) {
                    User user = JSON.parseObject(jsonUser, User.class);
                    UserHolder.setUser(user);
                    CacheUtils.expire(token.get(), COOKIE_EXPIRE_SECONDS);
                    return true;
                }
                //TODO 如果走到这里 可能是token过期了 没想好要怎么做
            }
        }
        //不应该走到这里
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        String error = JSON.toJSONString(ResultUtils.error(ResultCode.NO_AUTH_ERROR));
        PrintWriter writer = response.getWriter();
        writer.append(error);
        writer.flush();
        writer.close();
        return false;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(modelAndView == null) {
            //没有视图 前后端分离请求
            return;
        }
        User user = UserHolder.getUser();
        //补充js-api数据
        String requestURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        String url = StringUtils.isEmpty(queryString)? requestURL : requestURL + "?" + queryString;
        ModelMap modelMap = modelAndView.getModelMap();
        modelMap.addAttribute("user", user);
        wxService.jsApiSignature(modelAndView.getModelMap(), url);
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
}
