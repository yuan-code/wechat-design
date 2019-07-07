package com.hualala.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hualala.common.WXConstant;
import com.hualala.config.WXConfig;
import com.hualala.model.User;
import com.hualala.util.CacheUtils;
import com.hualala.util.HttpClientUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * 刷新微信公众号的access_token
     * https请求:
     * https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
     * 微信返回数据:
     * {"access_token":"ACCESS_TOKEN","expires_in":7200}
     *
     * @return
     */
    public String refreshToken() {
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppID());
        String url = String.format(WXConstant.ACCESS_TOKEN_URL, wxConfig.getAppID(), wxConfig.getSecret());
        HttpClientUtil.HttpResult result = HttpClientUtil.getInstance().post(url);
        log.info("获取微信公众号的access_token: {}", result.getContent());
        String accessToken = JSON.parseObject(result.getContent()).getString("access_token");
        CacheUtils.set(redisKey, accessToken, 5200);
        return accessToken;
    }

    /**
     * 获取公众号的AccessToken
     *
     * @return
     */
    public String getAccessToken() {
        // wechatOfficial:appID:accessToken
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppID());
        String accessToken = CacheUtils.get(redisKey);
        return accessToken;
    }


    /**
     * 通过 openID查询用户基本信息
     *
     * @param openID
     * @return
     */
    public User userBaseInfo(String openID) {
        String accessToken = getAccessToken();
        String url = String.format(WXConstant.USER_BASE_INFO_URL, accessToken, openID);
        HttpClientUtil.HttpResult result = HttpClientUtil.getInstance().post(url);
        log.info("通过OpenID来获取用户基本信息: {}",result.getContent());
        User user = JSONObject.parseObject(result.getContent(), User.class);
        return user;
    }

}
