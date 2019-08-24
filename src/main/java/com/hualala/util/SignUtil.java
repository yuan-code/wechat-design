package com.hualala.util;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.hualala.wechat.WXConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author YuanChong
 * @create 2019-08-11 15:49
 * @desc
 */
@Component
public class SignUtil {

    private static WXConfig wxConfig;

    @Autowired
    public void setWXConfig(WXConfig wxConfig) {
        SignUtil.wxConfig = wxConfig;
    }

    public static String genarate(Map<String, Object> params) {
        StringBuilder toSign = toSign(params);
        toSign.append("key=").append(wxConfig.getMchKey());
        return DigestUtils.md5Hex(toSign.toString()).toUpperCase();
    }


    public static String genarate(Object object) throws IllegalAccessException {
        TreeMap<String, Object> params = new TreeMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(object);
            JacksonXmlProperty annotation = field.getAnnotation(JacksonXmlProperty.class);
            String key = annotation.localName();
            if (value != null && !"sign".equals(key) && !"key".equals(key) && !"params".equals(key)) {
                params.put(key, value);
            }
        }
        StringBuilder toSign = toSign(params);
        toSign.append("key=").append(wxConfig.getMchKey());
        return DigestUtils.md5Hex(toSign.toString()).toUpperCase();
    }


    private static StringBuilder toSign(Map<String, Object> params) {
        StringBuilder toSign = new StringBuilder();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null && !"sign".equals(key) && !"key".equals(key) && !"params".equals(key)) {
                toSign.append(key).append("=").append(value).append("&");
            }
        }
        return toSign;
    }

}
