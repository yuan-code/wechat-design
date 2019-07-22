package com.hualala.util;

import com.hualala.config.CosConfig;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author YuanChong
 * @create 2019-07-22 18:33
 * @desc
 */
public class MediaUtils {


    private static CosConfig cosConfig;

    @Autowired
    public void setStringRedisTemplate(CosConfig cosConfig) {
        MediaUtils.cosConfig = cosConfig;
    }


    public static String upload(InputStream inputStream,String contentType) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(inputStream.available());
        objectMetadata.setContentType(contentType);
        String key = UUID.randomUUID().toString();
        PutObjectResult putObjectResult = CosConfig.COS_CLIENT.putObject(cosConfig.getBucket(), key, inputStream, objectMetadata);
        return key;
    }


    public static String uploadImage(InputStream inputStream) throws IOException {
        return upload(inputStream,"image/jpeg");
    }

}
