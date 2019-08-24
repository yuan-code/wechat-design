package com.hualala.wechat.common;


/**
 * @author YuanChong
 * @create 2018-07-04 18:56
 * @desc
 */
public class WXConstant {


    public static final String SUCCESS = "SUCCESS";

    /**
     * cookie name for access_token
     */
    public static final String COOKIE_ACCESS_TOKEN_NAME = "accessToken";


    /**
     * JS ticket 签名参数格式
     */
    public static final String JSAPI_SIGNATURE = "jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s";

    /**
     * 点击事件 热点推送
     */
    public static final String HOT_ARTICLE_CLICK_TYPE = "HotArticle";

    /**
     * 点击事件 联系我们
     */
    public static final String HOT_ARTICLE_CONTACT_US = "ContactUs";

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

    /**
     * 下载多媒体文件接口
     */
    public static final String DOWNLOAD_MEDIA = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";

    /**
     * 统一下单接口
     */
    public static final String WX_ORDER_PAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";

}
