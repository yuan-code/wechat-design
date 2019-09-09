package com.hualala.wechat.common;

/**
 * @author YuanChong
 * @create 2019-08-24 18:08
 * @desc
 */
public class RedisKey {

    /**
     * wx access_token redis key
     */
    public static final String ACCESS_TOKEN_KEY = "wechat:%s:accessToken";

    /**
     * wx JS ticket redis key
     */
    public static final String JSAPI_TICKET_KEY = "wechat:%s:jsapiTicket";

    /**
     * wx web access_token redis key
     */
    public static final String WEB_ACCESS_TOKEN_KEY = "wechat:%s:webAccessToken:%s";

    /**
     * 微信模板消息ID
     */
    public static final String TEMPLATEID_KEY = "wechat:%s:templateID:%s";

}
