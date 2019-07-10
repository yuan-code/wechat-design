package com.hualala.componet;

import com.alibaba.fastjson.JSONObject;
import com.hualala.util.ResultUtils;
import com.hualala.exception.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author YuanChong
 * @create 2019-06-01 09:34
 * @desc
 */
@Log4j2
@ControllerAdvice
public class WebExceptionHandler {

    /**
     * 全局异常捕捉处理
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public JSONObject errorHandler(Exception ex) {
        log.error("全局异常捕捉处理",ex);
        return ResultUtils.error(ex.toString());
    }

    /**
     * 拦截捕捉自定义异常 BusinessException.class
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public JSONObject businessErrorHandler(BusinessException ex) {
        return ResultUtils.error(ex);
    }
}
