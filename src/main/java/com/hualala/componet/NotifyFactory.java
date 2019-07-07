package com.hualala.componet;

import com.alibaba.fastjson.JSON;
import com.hualala.common.AIConstant;
import com.hualala.common.NotifyEnum;
import com.hualala.config.WXConfig;
import com.hualala.model.User;
import com.hualala.service.UserService;
import com.hualala.service.WXService;
import com.hualala.util.HttpClientUtil;
import com.hualala.util.TimeUtil;
import com.hualala.util.WXReply;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
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

    /**
     * 策略列表
     */
    private Map<NotifyEnum, WeChatNotify> notifyMap = new HashMap<>();

    /**
     * 工厂获取事件执行策略对象
     *
     * @param notifyType
     * @return
     */
    public WeChatNotify loadWeChatNotify(NotifyEnum notifyType) {
        WeChatNotify notify = notifyMap.get(notifyType);
        //对于没配置的策略 返回一个默认的空实现即可
        return Optional.ofNullable(notify).orElse((xmlMap) -> this.defaultNotify(xmlMap));
    }

    /**
     * 工厂提供默认空实现
     *
     * @param xmlMap
     * @return
     */
    public String defaultNotify(Map<String, String> xmlMap) {
        return "success";
    }


    /**
     * 扫描带有NotifyType注解的bean组装成map
     * 新加策略时 在类上加入注解@NotifyType(...)即可
     * 支持枚举多个策略事件
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> notifyBeanMap = applicationContext.getBeansWithAnnotation(NotifyType.class);
        Map<NotifyEnum[], WeChatNotify> annoValueBeanMap = notifyBeanMap.values().stream()
                .filter(obj -> ArrayUtils.contains(obj.getClass().getInterfaces(), WeChatNotify.class))
                .map(obj -> (WeChatNotify) obj)
                .collect(Collectors.toMap(obj -> obj.getClass().getAnnotation(NotifyType.class).value(), Function.identity()));

        annoValueBeanMap.entrySet().stream().forEach(enrty -> Arrays.stream(enrty.getKey()).forEach(type -> notifyMap.put(type, enrty.getValue())));
    }



    /*===============================下面用来定义具体的策略实现===============================*/

    /**
     * @author YuanChong
     * @create 2018-07-06 17:12
     * @desc 策略实现 用户关注
     */

    @Log4j2
    @NotifyType(NotifyEnum.SUBSCRIBE)
    public static class UnSubscribe implements WeChatNotify {

        @Autowired
        private WXService wxService;

        @Autowired
        private UserService userService;

        @Autowired
        private WXConfig wxConfig;

        @Override
        public String weChatNotify(Map<String, String> xmlMap) throws Exception {
            String openID = xmlMap.get("FromUserName");
            String appID = xmlMap.get("ToUserName");
            User user = wxService.userBaseInfo(openID);
            user.setAppid(wxConfig.getAppID());
            userService.saveUser(user);
            WXReply wxReply = new WXReply(appID, openID);
            return wxReply.replyMsg("我见青山多妩媚，料青山见我应如是");
        }
    }


    /**
     * @author YuanChong
     * @create 2019-01-15 17:12
     * @desc 策略实现 用户取关
     */
    @Log4j2
    @NotifyType(NotifyEnum.UNSUBSCRIBE)
    public static class Subscribe implements WeChatNotify {

        @Autowired
        private UserService userService;

        @Autowired
        private WXConfig wxConfig;

        @Override
        public String weChatNotify(Map<String, String> xmlMap) throws Exception {
            String openID = xmlMap.get("FromUserName");
            User user = new User();
            user.setOpenid(openID);
            user.setAppid(wxConfig.getAppID());
            user.setUnsubscribeTime(TimeUtil.currentDT());
            user.setSubscribeStatus(2);

            userService.updateUser(user);

            return "";
        }
    }


    /**
     * @author YuanChong
     * @create 2019-01-15 17:12
     * @desc 策略实现 点击菜单事件
     */
    @Log4j2
    @NotifyType(NotifyEnum.CLICK)
    public static class Click implements WeChatNotify {

        private final String mediaID = "ELYMY-79MurPtaqnYq7igIOKtsiVlENvokg06r0vR5E";

        @Override
        public String weChatNotify(Map<String, String> xmlMap) throws Exception {
            String openID = xmlMap.get("FromUserName");
            String appID = xmlMap.get("ToUserName");
            WXReply wxReply = new WXReply(appID, openID);
            return wxReply.replyImage(mediaID);
        }
    }

    /**
     * @author YuanChong
     * @create 2019-01-15 17:12
     * @desc 策略实现 用户发送文字事件
     */
    @Log4j2
    @NotifyType(NotifyEnum.TEXT)
    public static class Text implements WeChatNotify {


        @Value("${ai.apiKey}")
        private String aiKey;

        @Override
        public String weChatNotify(Map<String, String> xmlMap) throws Exception {

            String openID = xmlMap.get("FromUserName");
            String appID = xmlMap.get("ToUserName");
            WXReply wxReply = new WXReply(appID, openID);
            String content = xmlMap.get("Content");
            String aiUrl = String.format(AIConstant.TULING_API, aiKey, URLEncoder.encode(content, "utf-8"));
            HttpClientUtil.HttpResult result = HttpClientUtil.getInstance().post(aiUrl);
            String text = JSON.parseObject(result.getContent()).getString("text");
            int index = text.indexOf("http");
            if(index != -1) {
                text = text.substring(0,index);
            }
            return wxReply.replyMsg(text);
        }
    }

}
