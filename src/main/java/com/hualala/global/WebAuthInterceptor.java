package com.hualala.global;

import com.alibaba.fastjson.JSONObject;
import com.hualala.pay.OrderService;
import com.hualala.util.CurrentUser;
import com.hualala.user.UserService;
import com.hualala.user.domain.User;
import com.hualala.util.LockHelper;
import com.hualala.wechat.WXService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


/**
 * @author YuanChong
 * @create 2019-07-09 18:44
 * @desc 微信JS页面授权拦截器 用于获取当前点击页面的用户授权
 */
@Log4j2
@Component
public class WebAuthInterceptor implements HandlerInterceptor {


    @Autowired
    private UserService userService;

    @Autowired
    private WXService wxService;

    @Autowired
    private OrderService orderService;

    /**
     * cookie name for access_token
     */
    public static final String COOKIE_ACCESS_TOKEN_NAME = "accessToken";


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
            CurrentUser.setUser(cookieAuth);
            return true;
        }
        //授权码
        String code = request.getParameter("code");
        if (StringUtils.isEmpty(code)) {
            String requestURL = getUrl(request);
            String redirectUrl = wxService.preAuthUrl(requestURL);
            log.info("微信JS授权统一处理 redirectUrl=[{}]", redirectUrl);
            response.sendRedirect(redirectUrl);
            return false;
        }
        JSONObject result = wxService.webAccessToken(code);
        User user = wxService.webUserInfo(result.getString("access_token"), result.getString("openid"));
        User currentUser = userService.saveUser(user);
        //判断用户是否是有效的付费用户
        userService.vipAuth(currentUser);
        CurrentUser.setUser(currentUser);
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
        User user = CurrentUser.getUser();
        if (modelAndView != null) {
            ModelMap modelMap = modelAndView.getModelMap();
            modelMap.addAttribute("user", user);
            //有视图 freemarker请求
            //补充js-api数据
            String url = getUrl(request);
            wxService.jsApiSignature(modelAndView.getModelMap(), url);
        }
        //返回给前端cookie
        String cookieKey = userService.addSession(user);
        Cookie cookie = new Cookie(COOKIE_ACCESS_TOKEN_NAME, cookieKey);
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
        CurrentUser.clear();
    }


    /**
     * cookie认证
     *
     * @param request
     * @return
     */
    protected User cookieAuth(HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<String> tokenOptional = Arrays.stream(request.getCookies()).filter(c -> Objects.equals(c.getName(), COOKIE_ACCESS_TOKEN_NAME)).map(Cookie::getValue).findFirst();
            if (tokenOptional.isPresent()) {
                return userService.tokenAuth(tokenOptional.get());
            }
        }
        return null;
    }


    private String getUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        return StringUtils.isEmpty(queryString) ? requestURL.toString() : requestURL.append("?").append(queryString).toString();
    }

}
