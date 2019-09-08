package com.hualala.util;

import com.alibaba.fastjson.JSONObject;

/**
 * @author YuanChong
 * @create 2019-05-31 00:17
 * @desc
 */
public class ResultUtils {


    public static JSONObject success(Object object) {
        JSONObject result = new JSONObject();
        result.put("data",object);
        result.put("success",true);
        return result;
    }

    public static JSONObject success() {
        JSONObject result = new JSONObject();
        result.put("data",null);
        result.put("success",true);
        return result;
    }


    public static JSONObject error(String msg) {
        JSONObject result = new JSONObject();
        result.put("success",false);
        return result;
    }



}
