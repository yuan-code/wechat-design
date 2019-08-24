package com.hualala.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hualala.common.RedisKey;
import com.hualala.common.ResultCode;
import com.hualala.wechat.common.WXConstant;
import com.hualala.common.BusinessException;
import com.hualala.user.domain.User;
import com.hualala.pay.domain.WxPayRes;
import com.hualala.util.BeanParse;
import com.hualala.util.CacheUtils;
import com.hualala.util.HttpClientUtil;
import com.hualala.weixin.mp.JSApiUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

import static com.hualala.wechat.common.WXConstant.*;

/**
 * @author YuanChong
 * @create 2019-06-26 22:51
 * @desc
 */
@Log4j2
@Service
public class WXService {

    @Autowired
    private WXConfig wxConfig;


    /**
     * 获取公众号的AccessToken
     *
     * @return
     */
    public String getAccessToken() {
        String redisKey = String.format(RedisKey.ACCESS_TOKEN_KEY, wxConfig.getAppID());
        String accessToken = CacheUtils.get(redisKey);
        return accessToken;
    }

    /**
     * 获取公众号的JSTicket
     *
     * @return
     */
    public String getJSTicket() {
        String ticketKey = String.format(RedisKey.JSAPI_TICKET_KEY, wxConfig.getAppID());
        String ticket = CacheUtils.get(ticketKey);
        return ticket;
    }


    /**
     * 刷新微信公众号的access_token
     * https请求:
     * https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
     * 微信返回数据:
     * {"access_token":"ACCESS_TOKEN","expires_in":7200}
     *
     * @return
     */
    public String refreshToken() {
        String redisKey = String.format(RedisKey.ACCESS_TOKEN_KEY, wxConfig.getAppID());
        String url = String.format(ACCESS_TOKEN_URL, wxConfig.getAppID(), wxConfig.getSecret());
        HttpClientUtil.HttpResult result = HttpClientUtil.post(url);
        log.info("获取微信公众号的access_token: {}", result.getContent());
        String accessToken = JSON.parseObject(result.getContent()).getString("access_token");
        int expire = wxConfig.getExpire() + 200;
        CacheUtils.set(redisKey, accessToken, expire);
        return accessToken;
    }


    /**
     * 刷新jsapi_ticket
     * https请求:
     * https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi
     * 微信返回数据:
     * {"errcode":0,"errmsg":"ok","ticket":"JSAPI_TICKET","expires_in":7200}
     *
     * @return
     */
    public String refreshJSTicket() {
        String accessToken = getAccessToken();
        //获取JS-api ticket
        String ticketUrl = String.format(JSAPI_TICKET_URL, accessToken);
        HttpClientUtil.HttpResult ticketResult = HttpClientUtil.post(ticketUrl);
        log.info("获取微信公众号的jsApi ticket : {}", ticketResult.getContent());
        String ticket = JSON.parseObject(ticketResult.getContent()).getString("ticket");
        String ticketKey = String.format(RedisKey.JSAPI_TICKET_KEY, wxConfig.getAppID());
        int expire = wxConfig.getExpire() + 200;
        CacheUtils.set(ticketKey, ticket, expire);
        return ticket;
    }


    /**
     * 通过 openID查询用户基本信息
     * https请求:
     * https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
     *
     * @param openID
     * @return
     */
    public User userBaseInfo(String openID) {
        String accessToken = getAccessToken();
        String url = String.format(USER_BASE_INFO_URL, accessToken, openID);
        HttpClientUtil.HttpResult result = HttpClientUtil.post(url);
        log.info("通过OpenID来获取用户基本信息: {}", result.getContent());
        User user = JSONObject.parseObject(result.getContent(), User.class);
        if (StringUtils.isEmpty(user.getOpenid())) {
            log.error("通过OpenID来获取用户基本信息微信返回错误 url {} result {}", url, result);
            throw new BusinessException(ResultCode.WECHAT_ERROR);
        }
        return user;
    }

    /**
     * 通过code授权码换取网页授权access_token
     * https请求:
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     * 微信返回数据:
     * {"access_token":"ACCESS_TOKEN","expires_in":7200,"refresh_token":"REFRESH_TOKEN","openid":"OPENID","scope":"SCOPE"}
     *
     * @param code
     * @return
     */
    public JSONObject webAccessToken(String code) {
        String url = String.format(JS_ACCESS_TOKEN_URL, wxConfig.getAppID(), wxConfig.getSecret(), code);
        HttpClientUtil.HttpResult tokenResult = HttpClientUtil.post(url);
        log.info("通过code授权码换取网页授权access_token: {}", tokenResult.getContent());
        JSONObject result = JSONObject.parseObject(tokenResult.getContent());
        if (StringUtils.isEmpty(result.getString("access_token"))) {
            log.error("通过code授权码换取网页授权access_token微信返回错误 url {} result {}", url, result);
            throw new BusinessException(ResultCode.WECHAT_ERROR);
        }
        return result;
    }


    /**
     * 网页授权拉取用户信息(需scope为 snsapi_userinfo)
     * https请求:
     * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
     * 微信返回数据:
     * {"openid":" OPENID","nickname": NICKNAME,"sex":"1","province":"PROVINCE""city":"CITY","country":"COUNTRY","headimgurl":"","privilege":["PRIVILEGE1" "PRIVILEGE2"],"unionid":"UNIONID"}
     *
     * @param accessToken
     * @param openid
     * @return
     */
    public User webUserInfo(String accessToken, String openid) {
        String url = String.format(JS_USER_BASE_INFO_URL, accessToken, openid);
        HttpClientUtil.HttpResult result = HttpClientUtil.post(url);
        log.info("网页授权拉取用户信息: {}", result.getContent());
        User user = JSONObject.parseObject(result.getContent(), User.class);
        if (StringUtils.isEmpty(user.getOpenid())) {
            log.error("网页授权拉取用户信息微信返回错误 url {} result {}", url, result);
            throw new BusinessException(ResultCode.WECHAT_ERROR);
        }
        return user;
    }


    /**
     * 生成微信JS-SDK签名
     *
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public Map<String, Object> jsApiSignature(Map<String, Object> data, String url) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String noncestr = UUID.randomUUID().toString();
        String jsapiTicket = getJSTicket();
        String timestamp = Long.toString(System.currentTimeMillis());
        String params = String.format(JSAPI_SIGNATURE, jsapiTicket, noncestr, timestamp, url);
        //生成签名
        String signature = JSApiUtil.generateSignature(params);
        data.put("noncestr", noncestr);
        data.put("timestamp", timestamp);
        data.put("signature", signature);
        data.put("appID", wxConfig.getAppID());
        return data;
    }

    /**
     * 下载多媒体文件
     *
     * @param mediaID
     * @return
     * @throws Exception
     */
    public byte[] downloadMedia(String mediaID) {
        String accessToken = getAccessToken();
        String url = String.format(DOWNLOAD_MEDIA, accessToken, mediaID);
        return HttpClientUtil.downLoadFromUrl(url);
    }

    /**
     * 微信支付创建订单
     *
     * @param xml
     * @return
     */
    public WxPayRes payOrder(String xml) throws IOException {
        HttpClientUtil.HttpResult result = HttpClientUtil.postXML(WX_ORDER_PAY, xml);
        WxPayRes wxPayRes = BeanParse.XMLToBean(result.getContent(), WxPayRes.class);
        log.info("微信支付创建订单 wxPayRes: {}", wxPayRes);
        if (!wxPayRes.getReturnCode().equals(WXConstant.SUCCESS)) {
            throw new BusinessException(ResultCode.PAY_ERROR.getCode(), "【微信统一支付】发起支付, returnCode != SUCCESS, returnMsg = " + wxPayRes.getReturnMsg());
        }
        if (!wxPayRes.getResultCode().equals(WXConstant.SUCCESS)) {
            throw new BusinessException(ResultCode.PAY_ERROR.getCode(), "【微信统一支付】发起支付, resultCode != SUCCESS, err_code = " + wxPayRes.getErrCode() + " err_code_des=" + wxPayRes.getErrCodeDes());
        }
        return wxPayRes;
    }

}
