package com.hualala.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
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
@ConfigurationProperties(prefix = "cos")
public class CosConfig implements InitializingBean {

    private String secretID;
    private String secretKey;
    private String region;
    private String bucket;
    private String server;

    public static COSClient COS_CLIENT;

    @Override
    public void afterPropertiesSet() throws Exception {
        COSCredentials cred = new BasicCOSCredentials(secretID, secretKey);
        Region regionBean = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionBean);
        //  生成 cos 客户端。
        COS_CLIENT = new COSClient(cred, clientConfig);
    }
}
