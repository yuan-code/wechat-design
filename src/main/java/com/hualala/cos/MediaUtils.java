package com.hualala.cos;

import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author YuanChong
 * @create 2019-07-22 18:33
 * @desc
 */
@Component
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
        return cosConfig.getServer() + key;
    }


    public static String uploadImage(InputStream inputStream) throws IOException {
        return upload(inputStream,"image/jpeg");
    }

    public static String uploadImage(byte[] bytes) throws IOException {
        return upload(new ByteArrayInputStream(bytes),"image/jpeg");
    }
}
