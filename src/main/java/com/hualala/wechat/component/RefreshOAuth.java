package com.hualala.wechat.component;

import com.hualala.wechat.WXConfig;
import com.hualala.wechat.WXService;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author YuanChong
 * @create 2019-06-26 22:47
 * @desc
 */
@Component
public class RefreshOAuth implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private WXService wxService;

    @Autowired
    private WXConfig wxConfig;

    /**
     * 刷新token与js-ticket的定时线程
     */
    @Autowired
    private ScheduledThreadPoolExecutor oAuthPool;



    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        oAuthPool.scheduleAtFixedRate(() -> wxService.refreshToken(),0, wxConfig.getExpire(), TimeUnit.SECONDS);
        oAuthPool.scheduleAtFixedRate(() -> wxService.refreshJSTicket(),0, wxConfig.getExpire(), TimeUnit.SECONDS);

    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor oAuthPool() {
        return new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("refresh-wx-oath-%d").daemon(true).build());
    }


}
