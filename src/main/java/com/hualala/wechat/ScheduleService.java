package com.hualala.wechat;

import com.hualala.wechat.component.WXConfig;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author YuanChong
 * @create 2019-06-26 22:47
 * @desc
 */
@Component
public class ScheduleService implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private WXService wxService;

    @Autowired
    private WXConfig wxConfig;

    /**
     * 刷新token与js-ticket的定时线程
     */
    private ScheduledThreadPoolExecutor oAuthPool = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("refresh-wx-oath-%d").daemon(true).build());



    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        oAuthPool.scheduleAtFixedRate(() -> wxService.refreshToken(),0, wxConfig.getExpire(), TimeUnit.SECONDS);
        oAuthPool.scheduleAtFixedRate(() -> wxService.refreshJSTicket(),0, wxConfig.getExpire(), TimeUnit.SECONDS);
    }



}
