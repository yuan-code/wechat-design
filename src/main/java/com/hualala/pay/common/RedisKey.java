package com.hualala.pay.common;

/**
 * @author YuanChong
 * @create 2019-09-09 14:41
 * @desc
 */
public class RedisKey {

    /**
     * 用户付费订单
     */
    public static final String PAY_ORDER_KEY = "wechat:%s:payorder:%s:%s";


    public static final Long ORDER_EXPIRE_SECONDS = 60 * 60L;
}
