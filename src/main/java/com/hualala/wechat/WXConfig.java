package com.hualala.wechat;

import com.hualala.weixin.mp.WXBizMsgCrypt;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author YuanChong
 * @create 2019-06-26 22:20
 * @desc
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WXConfig implements InitializingBean {

    private String appID;
    private String secret;
    private String token;
    private String encodingAESKey;
    private Integer expire;

    private String mchId;
    private String mchKey;

    /**
     * 微信加解密工具
     */
    public static WXBizMsgCrypt WX_BIZ_MSG_CRYPT;

    @Override
    public void afterPropertiesSet() throws Exception {
        WX_BIZ_MSG_CRYPT = new WXBizMsgCrypt(token, encodingAESKey,appID);
    }

}
