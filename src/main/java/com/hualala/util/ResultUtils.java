package com.hualala.util;

import com.alibaba.fastjson.JSONObject;
import com.hualala.common.ResultCode;
import com.hualala.common.BusinessException;

/**
 * @author YuanChong
 * @create 2019-05-31 00:17
 * @desc
 */
public class ResultUtils {


    public static JSONObject success(Object object) {
        JSONObject result = new JSONObject();
        result.put("code", ResultCode.SUCCESS.getCode());
        result.put("msg",ResultCode.SUCCESS.getMsg());
        result.put("data",object);
        result.put("success",true);
        return result;
    }

    public static JSONObject success() {
        JSONObject result = new JSONObject();
        result.put("code",ResultCode.SUCCESS.getCode());
        result.put("msg",ResultCode.SUCCESS.getMsg());
        result.put("data",null);
        result.put("success",true);
        return result;
    }


    public static JSONObject error(String msg) {
        JSONObject result = new JSONObject();
        result.put("code",ResultCode.SYSTEM_ERROR.getCode());
        result.put("msg",msg);
        result.put("success",false);
        return result;
    }

    public static JSONObject error(BusinessException be) {
        JSONObject result = new JSONObject();
        result.put("code",be.getCode());
        result.put("msg",be.getMsg());
        result.put("success",false);
        return result;
    }

    public static JSONObject error(ResultCode resultCode) {
        JSONObject result = new JSONObject();
        result.put("code",resultCode.getCode());
        result.put("msg",resultCode.getMsg());
        result.put("success",false);
        return result;
    }

}
