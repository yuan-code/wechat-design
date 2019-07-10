package com.hualala.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hualala.common.WXConstant;
import com.hualala.config.WXConfig;
import com.hualala.model.User;
import com.hualala.service.WXService;
import com.hualala.util.CacheUtils;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.hualala.common.WXConstant.COOKIE_ACCESS_TOKEN_NAME;
import static com.hualala.common.WXConstant.WEB_ACCESS_TOKEN_KEY;


/**
 * @author YuanChong
 * @create 2019-07-09 18:44
 * @desc 微信JS页面授权拦截器
 */
@Component
public class WebAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private WXService wxService;

    @Autowired
    private WXConfig wxConfig;

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
        //授权码
//        String code = request.getParameter("code");
//        if (StringUtils.isEmpty(code)) {
//            StringBuffer requestURL = request.getRequestURL();
//            String encoderUrl = URLEncoder.encode(requestURL.toString(), StandardCharsets.UTF_8.name());
//            String redirectUrl = String.format(WXConstant.JS_PRE_AUTH_URL, wxConfig.getAppID(), encoderUrl, "snsapi_userinfo", request.getRequestURI());
//            response.sendRedirect(redirectUrl);
//            return false;
//        }
//        JSONObject tokenMap = wxService.webAccessToken(code);
//        String accessToken = tokenMap.getString("access_token");
//        String openid = tokenMap.getString("openid");
//        Integer expiresIn = tokenMap.getInteger("expires_in");
//        String tokenKey = String.format(WEB_ACCESS_TOKEN_KEY, wxConfig.getAppID(),openid);
//        //先保存起来这个web token 暂时没什么用
//        CacheUtils.set(tokenKey,tokenMap.toJSONString(),expiresIn);
//        User user = wxService.webUserInfo(accessToken, openid);
//        UserHolder.setUser(user);
//        //返回给前端cookie
//        //cookie内的token30分钟过期
//        String cookieToken = UUID.randomUUID().toString();
//        CacheUtils.set(cookieToken, JSON.toJSONString(user),30 * 60);
//        Cookie cookie = new Cookie(COOKIE_ACCESS_TOKEN_NAME,cookieToken);
//        response.addCookie(cookie);
        return true;
    }

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
        //拿到的这个url是不带？后面的参数的  项目应避免使用GET请求传参数
        StringBuffer requestURL = request.getRequestURL();
        String encoderUrl = URLEncoder.encode(requestURL.toString(), StandardCharsets.UTF_8.name());
        ModelMap modelMap = modelAndView.getModelMap();
        modelMap.addAttribute("user",UserHolder.getUser());
        //补充js-api数据
        wxService.jsApiSignature(modelAndView.getModelMap(), encoderUrl);
    }

    /**
     * 清除user 防内存泄露
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.clear();
    }
}
