package com.hualala.common;


/**
 * @author YuanChong
 * @create 2018-07-04 18:56
 * @desc
 */
public class WXConstant {

    /**
     * 微信公众号access_token
     */
    public static final String ACCESS_TOKEN_KEY = "wechat:accessToken:%s";

    /**
     * 获取微信公众号的access_token
     */
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**
     * 获取关注了公众号的微信的信息
     */
    public static final String USER_BASE_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

}
