package com.hualala.common;


/**
 * @author YuanChong
 * @create 2018-07-04 18:56
 * @desc
 */
public class WXConstant {

    /**
     * cookie name for access_token
     */
    public static final String COOKIE_ACCESS_TOKEN_NAME = "accessToken";

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
     * JS ticket 签名参数格式
     */
    public static final String JSAPI_SIGNATURE = "jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s";

    /**
     * 获取微信公众号的access_token
     */
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**
     * 获取关注了公众号的微信的信息
     */
    public static final String USER_BASE_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

    /**
     * 获取微信公众号的js-api ticket
     */
    public static final String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";


    /**
     * JS网页授权重定向地址
     */
    public static final String JS_PRE_AUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";

    /**
     * JS网页授权accessToken
     */
    public static final String JS_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * JS网页授权accessToken获取用户信息
     */
    public static final String JS_USER_BASE_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
}
