package com.hualala.interceptor;

import com.alibaba.fastjson.JSON;
import com.hualala.common.ResultCode;
import com.hualala.model.User;
import com.hualala.util.ResultUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


/**
 * @author YuanChong
 * @create 2019-07-09 18:44
 * @desc 授权后的内部页面拦截器 用于拦截用户登录状态
 */
@Component
public class PassportInterceptor extends AbstractInterceptor {


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
        //cookie 认证
        User user = super.cookieAuth(request);
        if(user != null) {
            return true;
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



}
