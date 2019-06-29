package com.hualala.componet;

import com.hualala.common.NotifyEnum;
import com.hualala.config.WXConfig;
import com.hualala.service.WXService;
import com.hualala.util.XMLParse;
import com.hualala.weixin.mp.WXBizMsgCrypt;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;
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
    public WeChatNotify findWeChatNotify(NotifyEnum notifyType) {
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
     * @desc 策略实现 用户取消关注
     */

    @Log4j2
    @NotifyType(NotifyEnum.UNSUBSCRIBE)
    public static class UnAuthorized implements WeChatNotify {

        @Autowired
        private WXService wxService;

        @Override
        public String weChatNotify(Map<String, String> xmlMap) throws Exception {
            return "";
        }
    }


    /**
     * @author YuanChong
     * @create 2019-01-15 17:12
     * @desc 策略实现 用户关注事件
     */
    @Log4j2
    @NotifyType(NotifyEnum.SUBSCRIBE)
    public static class Subscribe implements WeChatNotify {

        @Autowired
        private WXService wxService;

        @Override
        public String weChatNotify(Map<String, String> xmlMap) throws Exception {
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

        @Autowired
        private WXConfig wxConfig;

        @Override
        public String weChatNotify(Map<String, String> xmlMap) throws Exception {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("ToUserName", xmlMap.get("FromUserName"));
            resultMap.put("FromUserName", xmlMap.get("ToUserName"));
            String timeStamp = Long.toString(System.currentTimeMillis());
            resultMap.put("CreateTime", timeStamp);
            resultMap.put("MsgType", "image");
            Map<String, String> image = new HashMap<>();
            image.put("MediaId", "WDjzyoFoPGQhUt5qs01mWHpkdgRFEv-hL4NdsYWHGwbbxfXXJtkYzAmYms1mNHOr");
            resultMap.put("Image", image);
            String replyMsg = XMLParse.mapToXml(resultMap);
            WXBizMsgCrypt pc = wxConfig.getWxBizMsgCrypt();
            //微信使用时间戳加随机数的方式来防止攻击(如果两次请求的时间戳+随机数都相同)
            String result = pc.encryptMsg(replyMsg, "1561786796206", Double.toString(Math.random()));
            return result;
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

        @Autowired
        private WXConfig wxConfig;

        @Override
        public String weChatNotify(Map<String, String> xmlMap) throws Exception {

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("ToUserName", xmlMap.get("FromUserName"));
            resultMap.put("FromUserName", xmlMap.get("ToUserName"));
            String timeStamp = Long.toString(System.currentTimeMillis());
            resultMap.put("CreateTime", timeStamp);

            switch (xmlMap.get("Content")) {
                case "1":
                    resultMap.put("MsgType", "text");
                    resultMap.put("Content", "你发的是1");
                    break;
                case "2":
                    resultMap.put("MsgType", "image");
                    Map<String, String> image = new HashMap<>();
                    image.put("MediaId", "WDjzyoFoPGQhUt5qs01mWHpkdgRFEv-hL4NdsYWHGwbbxfXXJtkYzAmYms1mNHOr");
                    resultMap.put("Image", image);
                    break;
                case "3":
                    resultMap.put("MsgType", "news");
                    resultMap.put("ArticleCount", "1");
                    Map<String, Object> articles = new HashMap<>();
                    Map<String, Object> item = new HashMap<>();
                    item.put("Title", "这是一个标题");
                    item.put("Description", "这是一个描述");
                    item.put("PicUrl", "http://res.hualala.com/group2/M01/07/9B/wKgVSluXLGq5xwjdAAAnjOoyunk680.png");
                    item.put("Url", "http://shop.hualala.com");
                    articles.put("item", item);
                    resultMap.put("Articles", articles);
                    break;
                default:
                    resultMap.put("MsgType", "text");
                    resultMap.put("Content", "请发1或2或3");
                    break;
            }
            String replyMsg = XMLParse.mapToXml(resultMap);
            log.info("被动回复公众号用户消息=====>>>>> {}", replyMsg);
            WXBizMsgCrypt pc = wxConfig.getWxBizMsgCrypt();
            //微信使用时间戳加随机数的方式来防止攻击(如果两次请求的时间戳+随机数都相同)
            String result = pc.encryptMsg(replyMsg, "1561786796206", Double.toString(Math.random()));
            return result;
        }
    }

}
