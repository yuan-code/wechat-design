package com.hualala.wechat.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YuanChong
 * @create 2018-07-31 15:27
 * @desc  微信的推送类型注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface NotifyType {

    /**
     * 事件推送的类型 支持枚举多个事件
     * @return
     */
    NotifyEnum[] value();
}
