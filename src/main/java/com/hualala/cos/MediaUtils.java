package com.hualala.cos;

import com.hualala.util.HttpClientUtil;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
@Component
public class MediaUtils {


    private static CosConfig cosConfig;

    @Autowired
    public void setStringRedisTemplate(CosConfig cosConfig) {
        MediaUtils.cosConfig = cosConfig;
    }


    public static String upload(InputStream inputStream, String contentType) {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setContentType(contentType);
            String key = UUID.randomUUID().toString();
            PutObjectResult putObjectResult = CosConfig.COS_CLIENT.putObject(cosConfig.getBucket(), key, inputStream, objectMetadata);
            return cosConfig.getServer() + key;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String uploadImage(InputStream inputStream) {
        return upload(inputStream, "image/jpeg");
    }

    public static String uploadImage(byte[] bytes) {
        return upload(new ByteArrayInputStream(bytes), "image/jpeg");
    }

    public static String uploadImage(String url) {
        try {
            byte[] bytes = HttpClientUtil.downLoadFromUrl(url);
            return MediaUtils.uploadImage(bytes);
        } catch (Exception e) {
            log.error("url = {} 下载失败", url, e);
            return url;
        }
    }

}
