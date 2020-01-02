package com.hualala.wechat.component;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hualala.article.ArticleService;
import com.hualala.article.domain.Article;
import com.hualala.user.UserService;
import com.hualala.user.domain.User;
import com.hualala.util.HttpClientUtil;
import com.hualala.util.LockHelper;
import com.hualala.util.TimeUtil;
import com.hualala.wechat.WXService;
import com.hualala.wechat.common.NotifyEnum;
import com.hualala.wechat.common.NotifyType;
import com.hualala.wechat.common.ReplyMsg;
import com.hualala.wechat.common.WXConstant;
import com.hualala.wechat.util.WXReply;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hualala.wechat.common.WXConstant.*;

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
    private Map<NotifyEnum, WechatNotify> notifyMap = new HashMap<>();

    /**
     * 工厂获取事件执行策略对象
     *
     * @param notifyType
     * @return
     */
    public WechatNotify loadWeChatNotify(NotifyEnum notifyType) {
        WechatNotify notify = notifyMap.get(notifyType);
        //对于没配置的策略 返回一个默认的空实现即可
        return notify == null ? xmlMap -> "success" : notify;
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
        Map<NotifyEnum[], WechatNotify> annoValueBeanMap = notifyBeanMap.values().stream()
                .filter(obj -> ArrayUtils.contains(obj.getClass().getInterfaces(), WechatNotify.class))
                .map(obj -> (WechatNotify) obj)
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
    @NotifyType({NotifyEnum.SUBSCRIBE, NotifyEnum.SCAN})
    public static class Subscribe implements WechatNotify {

        @Autowired
        private WXService wxService;

        @Autowired
        private UserService userService;


        @Override
        public String wechatNotify(Map<String, String> xmlMap) throws Exception {
            String openID = xmlMap.get("FromUserName");
            String appID = xmlMap.get("ToUserName");
            User user = wxService.userBaseInfo(openID);
            userService.saveUser(user);
            //增加代理人
            Optional.ofNullable(xmlMap.get("EventKey"))
                    .map(eventKey -> eventKey.startsWith("qrscene_") ? eventKey.substring(8) : eventKey)
                    .filter(eventKey -> !Objects.equals(eventKey,openID))
                    .ifPresent(eventKey -> userService.recommend(eventKey,openID));
            userService.deleteSession(user.getOpenid());
            WXReply wxReply = new WXReply(appID, openID);
            return wxReply.replyMsg(ReplyMsg.SUBSCRIBE_MSG);
        }
    }


    /**
     * @author YuanChong
     * @create 2019-01-15 17:12
     * @desc 策略实现 用户取关
     */
    @Log4j2
    @NotifyType(NotifyEnum.UNSUBSCRIBE)
    public static class UnSubscribe implements WechatNotify {

        @Autowired
        private UserService userService;

        @Autowired
        private WXConfig wxConfig;

        @Override
        public String wechatNotify(Map<String, String> xmlMap) throws Exception {
            String openID = xmlMap.get("FromUserName");
            User user = new User();
            user.setUnsubscribeTime(TimeUtil.currentDT());
            user.setSubscribeStatus(2);
            UpdateWrapper<User> wrapper = new UpdateWrapper<User>().eq("appid", wxConfig.getAppID()).eq("openid", openID);
            userService.update(user, wrapper);
            userService.deleteSession(openID);
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
    public static class Click implements WechatNotify {

        private final String mediaID = "ELYMY-79MurPtaqnYq7igNohXkc_8oj6bSCngGo1ZCE";

        @Autowired
        private ArticleService articleService;

        @Autowired
        private WXService wxService;


        @Override
        public String wechatNotify(Map<String, String> xmlMap) throws Exception {
            String openID = xmlMap.get("FromUserName");
            String appID = xmlMap.get("ToUserName");
            WXReply wxReply = new WXReply(appID, openID);
            switch (xmlMap.get("EventKey")) {
                case HOT_ARTICLE_CLICK:
                    Article article = articleService.findAny();
                    return wxReply.replyNews(article.getTitle(), article.getSummary(), article.getThumbnail(), article.resolveUrl());
                case CONTACT_US_CLICK:
                    return wxReply.replyImage(mediaID);
                case AGENT_PARTNER_CLICK:
                    //通过客服消息发送用户唯一二维码 30天过期
                    wxService.asynSendQrcode(openID);
                    return wxReply.replyMsg(ReplyMsg.AGENT_MSG);
                default:
                    return "";
            }
        }
    }

    /**
     * @author YuanChong
     * @create 2019-01-15 17:12
     * @desc 策略实现 用户发送文字事件
     */
    @Log4j2
    @NotifyType(NotifyEnum.TEXT)
    public static class Text implements WechatNotify {


        @Autowired
        private ArticleService articleService;

        @Autowired
        private LockHelper lockHelper;

        @Override
        public String wechatNotify(Map<String, String> xmlMap) throws Exception {

            String openID = xmlMap.get("FromUserName");
            String appID = xmlMap.get("ToUserName");
            WXReply wxReply = new WXReply(appID, openID);
            String content = xmlMap.get("Content");
            if (content.startsWith("https://mp.weixin.qq.com/")) {
                String lockKey = "copyArticle:" + URLEncoder.encode(content, "UTF-8");
                Article article = lockHelper.doSync(lockKey, () -> articleService.articleCopy(content,openID));
                return wxReply.replyNews(article.getTitle(), article.getSummary(), article.getThumbnail(), article.resolveUrl());
            }
            String msg = "回复公众号，内容为要复制的文章链接地址，即可获得文章推送（仅支持mp.weixin.qq.com域名下的原创文章）";
            return wxReply.replyMsg(msg);
        }
    }

}
