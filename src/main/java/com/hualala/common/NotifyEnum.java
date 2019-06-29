package com.hualala.common;


import java.util.Objects;

/**
 * @author YuanChong
 * @create 2018-07-31 15:27
 * @desc 微信的推送类型枚举
 */
public enum NotifyEnum {

    //菜单点击事件
    CLICK("event", "CLICK"),
    //关注
    SUBSCRIBE("event", "subscribe"),
    //取关
    UNSUBSCRIBE("event", "unsubscribe"),
    //已关注时的扫码事件
    SCAN("event", "SCAN"),
    //文字消息回复
    TEXT("text", null);


    private String msgType;
    private String event;

    NotifyEnum(String msgType, String event) {
        this.msgType = msgType;
        this.event = event;
    }

    public String getMsgType() {
        return this.msgType;
    }

    public String getEvent() {
        return this.event;
    }

    /**
     * 解析事件类型
     *
     * @param msgType
     * @param event
     * @return
     */
    public static NotifyEnum resolveEvent(String msgType, String event) {
        for (NotifyEnum notifyEnum : NotifyEnum.values()) {
            if (Objects.equals(msgType, notifyEnum.getMsgType()) && Objects.equals(event, notifyEnum.getEvent())) {
                return notifyEnum;
            }
        }
        return null;
    }
}
