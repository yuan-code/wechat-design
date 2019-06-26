package com.hualala.componet;

import com.hualala.config.NotifyType;
import com.hualala.service.WXService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author YuanChong
 * @create 2018-07-31 15:43
 * @desc
 */
@Component
public class NotifyFactory implements ApplicationContextAware {

    //策略列表
    private Map<String, WeChatNotify> notifyMap = new HashMap<>();


    public WeChatNotify createWeChatNotify(String notifyType) {
        WeChatNotify notify = notifyMap.get(notifyType);
        //对于没配置的策略 返回一个默认的空实现即可
        return Optional.ofNullable(notify).orElse((xmlMap) -> {});
    }

    /**
     * 扫描带有NotifyType注解的bean组装成map
     * 新加策略时  在类上加入注解@NotifyType("xxxx")即可
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> notifyBeanMap = applicationContext.getBeansWithAnnotation(NotifyType.class);
        Map<String[], WeChatNotify> annoValueBeanMap = notifyBeanMap.values().stream()
                .filter(obj -> ArrayUtils.contains(obj.getClass().getInterfaces(), WeChatNotify.class))
                .map(obj -> (WeChatNotify) obj)
                .collect(Collectors.toMap(obj -> obj.getClass().getAnnotation(NotifyType.class).value(), Function.identity()));

        annoValueBeanMap.entrySet().stream().forEach(enrty -> Arrays.stream(enrty.getKey()).forEach(type -> notifyMap.put(type, enrty.getValue())));
    }


    /**
     * @author YuanChong
     * @create 2018-07-06 16:21
     * @desc 授权事件
     */
    public interface WeChatNotify {

        void weChatNotify(Map<String, String> xmlMap) throws Exception;
    }


    /**
     * @author YuanChong
     * @create 2018-07-06 17:12
     * @desc 策略类 授权相关 公众号取消授权
     */

    @Log4j2
    @NotifyType("unauthorized")
    public static class UnAuthorized implements WeChatNotify {

        @Autowired
        private WXService wxService;

        @Override
        public void weChatNotify(Map<String, String> xmlMap) throws Exception {

        }
    }


    /**
     * @author YuanChong
     * @create 2019-01-15 17:12
     * @desc 策略类 授权相关 用户关注事件
     * 1. 扫描带参数的二维码
     * 2. 搜索关注
     * 3. ...
     */
    @Log4j2
    @NotifyType({"subscribe", "SCAN"})
    public static class Subscribe implements WeChatNotify {

        @Autowired
        private WXService wxService;

        @Override
        public void weChatNotify(Map<String, String> xmlMap) throws Exception {

        }
    }

}
