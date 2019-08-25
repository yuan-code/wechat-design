package com.hualala.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hualala.common.RedisKey;
import com.hualala.order.domain.Order;
import com.hualala.user.domain.User;
import com.hualala.order.OrderService;
import com.hualala.user.UserService;
import com.hualala.wechat.WXConfig;
import com.hualala.wechat.WXService;
import com.hualala.util.CacheUtils;
import com.hualala.wechat.util.UserHolder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.hualala.wechat.common.WXConstant.COOKIE_ACCESS_TOKEN_NAME;
import static com.hualala.wechat.common.WXConstant.JS_PRE_AUTH_URL;


/**
 * @author YuanChong
 * @create 2019-07-09 18:44
 * @desc 微信JS页面授权拦截器 用于获取当前点击页面的用户授权
 */
@Log4j2
@Component
public class WebAuthInterceptor implements HandlerInterceptor {


    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private WXService wxService;

    @Autowired
    private OrderService orderService;


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
        User cookieAuth = this.cookieAuth(request);
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
        User newUser = userService.saveUser(user);
        //判断用户是否是有效的付费用户
        orderService.currentUserOrder(newUser.getOpenid()).ifPresent(order -> newUser.setAvailable(true));
        UserHolder.setUser(newUser);
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
        User user = UserHolder.getUser();
        if (modelAndView != null) {
            ModelMap modelMap = modelAndView.getModelMap();
            modelMap.addAttribute("user", user);
            //有视图 freemarker请求
            //补充js-api数据
            String requestURL = request.getRequestURL().toString();
            String queryString = request.getQueryString();
            String url = StringUtils.isEmpty(queryString) ? requestURL : requestURL + "?" + queryString;
            wxService.jsApiSignature(modelAndView.getModelMap(), url);
        }
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
                    //判断用户是否是有效的付费用户
                    UserHolder.setUser(user);
                    CacheUtils.expire(token.get(), RedisKey.COOKIE_EXPIRE_SECONDS);
                    orderService.currentUserOrder(user.getOpenid()).ifPresent(order -> user.setAvailable(true));
                    return user;
                }
            }
        }
        return null;
    }

}
