package com.hualala.common;


/**
 * @author YuanChong
 * @create 2018-07-04 18:56
 * @desc
 */
public class WXConstant {

    //微信公众号access_token
    public static final String ACCESS_TOKEN_KEY = "wechat:accessToken:%s";

    //获取微信公众号的access_token
    public static final String WX_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    //获取二维码的ticket url
    public static final String QR_CODE_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
    //获取关注了公众号的微信的信息
    public static final String WX_USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
    //微信扫码绑定状态key
    public static final String WX_BINDING_KEY = "supplychain:wxBindingStatus:%s:%s:%s:%s";

}
