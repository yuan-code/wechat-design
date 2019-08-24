package com.hualala.common;

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
     * token过期时间
     */
    public static final Long COOKIE_EXPIRE_SECONDS = 60 * 60L;

    /**
     * 用户付费订单
     */
    public static final String PAY_ORDER_KEY = "wechat:%s:payorder:%s:%s";


    public static final Long ORDER_EXPIRE_SECONDS = 60 * 60L;
}
