package com.hualala.config;

import lombok.Data;
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
public class WXConfig {

    private String appID;
    private String secret;
    private String token;
    private String encodingAESKey;

}
