package com.hualala.pay.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author YuanChong
 * @create 2019-09-09 15:40
 * @desc
 */
@Data
@Component
@ConfigurationProperties(prefix = "pay")
public class PayConfig {

    private String mchId;
    private String mchKey;
}
