package com.hualala.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hualala.common.RedisKey;
import com.hualala.config.WXConfig;
import com.hualala.model.User;
import com.hualala.service.UserService;
import com.hualala.service.WXService;
import com.hualala.util.CacheUtils;
import com.hualala.util.UserHolder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.hualala.common.WXConstant.*;


/**
 * @author YuanChong
 * @create 2019-07-09 18:44
 * @desc 微信JS页面授权拦截器 用于获取当前点击页面的用户授权
 */
@Log4j2
@Component
public class WebAuthInterceptor extends AbstractInterceptor {


    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private UserService userService;

    /**
     * 微信JS授权统一处理
     * 存在已经存在cookie了 则不需要走授权 优化效率
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User cookieAuth = super.cookieAuth(request);
        if(cookieAuth != null) {
            return true;
        }
        //授权码
        String code = request.getParameter("code");
        if (StringUtils.isEmpty(code)) {
            StringBuffer requestURL = request.getRequestURL();
            String encoderUrl = URLEncoder.encode(requestURL.toString(), StandardCharsets.UTF_8.name());
            String redirectUrl = String.format(JS_PRE_AUTH_URL, wxConfig.getAppID(), encoderUrl, "snsapi_userinfo", request.getRequestURI());
            log.info("微信JS授权统一处理 redirectUrl=[{}]", redirectUrl);
            response.sendRedirect(redirectUrl);
            return false;
        }
        JSONObject tokenMap = wxService.webAccessToken(code);
        String accessToken = tokenMap.getString("access_token");
        String openid = tokenMap.getString("openid");
        Integer expiresIn = tokenMap.getInteger("expires_in");
        String tokenKey = String.format(RedisKey.WEB_ACCESS_TOKEN_KEY, wxConfig.getAppID(), openid);
        //先保存起来这个web token 暂时没什么用
        CacheUtils.set(tokenKey, tokenMap.toJSONString(), expiresIn);
        User user = wxService.webUserInfo(accessToken, openid);
        user = userService.saveUser(user);
        UserHolder.setUser(user);
        return true;
    }


    /**
     * 设置cookie
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request,response,handler,modelAndView);
        User user = UserHolder.getUser();
        //返回给前端cookie
        //cookie内的token一小时过期
        String cookieToken = DigestUtils.md5Hex(user.getAppid() + user.getOpenid());
        user.setToken(cookieToken);
        if (CacheUtils.exists(cookieToken)) {
            CacheUtils.expire(cookieToken, RedisKey.COOKIE_EXPIRE_SECONDS);
        } else {
            CacheUtils.set(cookieToken, JSON.toJSONString(user), RedisKey.COOKIE_EXPIRE_SECONDS);
        }
        Cookie cookie = new Cookie(COOKIE_ACCESS_TOKEN_NAME, cookieToken);
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
