package com.hualala.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * @author YuanChong
 * @create 2019-06-01 09:34
 * @desc
 */
@Log4j2
@Controller
public class GlobalExceptionHandler implements ErrorController {


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request){
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(statusCode == 401){
            return "error/401";
        }else if(statusCode == 404){
            return "error/404";
        }else if(statusCode == 403){
            return "error/403";
        }else{
            return "error/500";
        }

    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
