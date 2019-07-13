package com.hualala.componet;

import java.util.Map;

/**
 * @author YuanChong
 * @create 2018-07-06 16:21
 * @desc 微信事件推送策略的最上层抽象
 */
public interface WechatNotify {


    /**
     * 上层事件推送策略抽象接口
     *
     * @param xmlMap
     * @return
     * @throws Exception
     */
    String wechatNotify(Map<String, String> xmlMap) throws Exception;

}
