package com.hualala.pay;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.common.BusinessException;
import com.hualala.common.RedisKey;
import com.hualala.common.ResultCode;
import com.hualala.mail.MailService;
import com.hualala.pay.domain.Order;
import com.hualala.pay.domain.WXPayResult;
import com.hualala.pay.domain.WxPayReq;
import com.hualala.pay.domain.WxPayRes;
import com.hualala.util.BeanParse;
import com.hualala.util.CacheUtils;
import com.hualala.util.TimeUtil;
import com.hualala.wechat.WXConfig;
import com.hualala.pay.util.MoneyUtil;
import com.hualala.wechat.WXService;
import freemarker.template.utility.CollectionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YuanChong
 * @create 2019-08-04 08:43
 * @desc
 */
@Log4j2
@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private WXService wxService;

    @Autowired
    private MailService mailService;

    @Value("#{'${spring.mail.toUser}'.split(',')}")
    private List<String> mailUser;


    /**
     * 创建订单
     *
     * @param vipType
     */
    @Transactional(rollbackFor = Exception.class)
    public WxPayRes create(Integer vipType) throws Exception {
        Order order = new Order();
        order.setOrderType(vipType);
        order.setMchid(wxConfig.getMchId());
        //生成订单号 计算金额 绑定用户 IP
        order.generateNo().computeMoney().findIP().binding();
        orderMapper.insert(order);
        //微信支付订单
        WxPayReq wxPayOrder = new WxPayReq(order);
        wxPayOrder.buildSortMap().sign();
        String xml = BeanParse.beanToXMl(wxPayOrder);
        WxPayRes wxPayRes = wxService.payOrder(xml);
        return wxPayRes;
    }

    /**
     * 支付成功 订单会被缓存 这里的缓存仅仅起到了缓存的作用 缓存失效会从DB重新查
     *
     * @param payResult
     */
    @Transactional(rollbackFor = Exception.class)
    public void paySuccess(WXPayResult payResult) throws ParseException {
        Wrapper<Order> wrapper = new QueryWrapper<Order>()
                .eq("appid", payResult.getAppid())
                .eq("openid", payResult.getOpenid())
                .eq("mchid", payResult.getMchid())
                .eq("order_no", payResult.getOutTradeNo());
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new BusinessException(ResultCode.PAY_ERROR);
        }
        //如果有重复支付，计算订单的下次起止时间
        Long beginTime = payResult.getTimeEnd();
        //查询vip结束时间
        Long vipEndTime = selectVipEndTime(payResult.getOpenid(), order.getOrderNo());
        if (vipEndTime != null && vipEndTime >= beginTime) {
            beginTime = TimeUtil.stepTime(vipEndTime, Calendar.SECOND, 1);
        }
        order.validateMoney(MoneyUtil.Fen2Yuan(payResult.getCashFee()));
        order.calculateTime(beginTime).savePayResult(payResult);
        orderMapper.updateById(order);
        //缓存订单
        cacheOrder(order);

        String msg = "有人购买会员了，openID={} 支付金额={}元 order信息:{}";
        String content = String.format(msg,order.getOpenid(),order.getCashFee(),JSON.toJSONString(order));
        mailUser.stream().forEach(toUser -> mailService.sendMail(toUser,"青山高创公众号新增一个会员订单",content));
    }

    /**
     * 查询vip结束时间
     * @param openid
     * @param excludeNo
     * @return
     */
    public Long selectVipEndTime(String openid, String excludeNo) {
        return orderMapper.selectVipEndTime(wxConfig.getAppID(), openid, excludeNo);
    }

    /**
     * 获取用户在当前时间生效的订单
     *
     * @param openid
     * @return
     */
    public Optional<Order> currentUserOrder(String openid) {
        List<Order> orderList = successOrder(openid, 0, -1);
        Long currentTime = TimeUtil.currentDT();
        return orderList.stream().filter(order -> currentTime >= order.getBeginTime() && currentTime <= order.getEndTime()).findAny();
    }

    /**
     * 获取支付成功的订单
     *
     * @param openid
     * @return
     */
    public List<Order> successOrder(String openid, Integer start, Integer end) {
        if (validateCacheOrder(openid)) {
            try {
                String redisKey = String.format(RedisKey.PAY_ORDER_KEY, wxConfig.getAppID(), wxConfig.getMchId(), openid);
                Set<String> jsonSet = CacheUtils.zRangeRevertAll(redisKey, start, end);
                return jsonSet.stream().map(json -> JSON.parseObject(json, Order.class)).collect(Collectors.toList());
            } catch (Exception e) {
                //do nothing
                log.error("获取最后一次订单出现异常，可能是缓存挂了", e);
            }
        }
        List<Order> orderList = queryDBSuccessOrder(openid);
        orderList.stream().forEach(order -> cacheOrder(order));
        orderList = orderList.stream().sorted(Comparator.comparing(Order::getEndTime).reversed()).collect(Collectors.toList());
        if (start < 0) {
            start = 0;
        }
        if (end > orderList.size()) {
            end = orderList.size();
        }
        if (start > end) {
            start = end;
        }
        return end == -1 ? orderList : orderList.subList(start, end);
    }


    /**
     * 缓存订单
     *
     * @param order
     */
    private void cacheOrder(Order order) {
        try {
            String redisKey = String.format(RedisKey.PAY_ORDER_KEY, wxConfig.getAppID(), wxConfig.getMchId(), order.getOpenid());
            CacheUtils.zAdd(redisKey, JSON.toJSONString(order), order.getEndTime().doubleValue());
            CacheUtils.expire(redisKey, RedisKey.ORDER_EXPIRE_SECONDS);
        } catch (Exception e) {
            //do nothing
            log.error("缓存订单出现异常，可能是缓存挂了", e);
        }
    }


    /**
     * 验证缓存数据是否同步 加缓存的原则是redis挂了不会影响到正常的业务
     *
     * @param openid
     * @return
     */
    private Boolean validateCacheOrder(String openid) {
        try {
            Wrapper<Order> wrapper = new QueryWrapper<Order>()
                    .eq("appid", wxConfig.getAppID())
                    .eq("openid", openid)
                    .eq("mchid", wxConfig.getMchId())
                    .eq("status", 2);
            Integer dbCount = orderMapper.selectCount(wrapper);
            String redisKey = String.format(RedisKey.PAY_ORDER_KEY, wxConfig.getAppID(), wxConfig.getMchId(), openid);
            Long redisCount = CacheUtils.zSize(redisKey);
            boolean result = dbCount.equals(redisCount.intValue());
            if (!result) {
                CacheUtils.zDel(redisKey);
            }
            return result;
        } catch (Exception e) {
            log.error("验证缓存数据是否同步出现异常，可能是缓存挂了", e);
            return false;
        }
    }

    /**
     * 从数据库中查询支付的订单
     *
     * @param openid
     * @return
     */
    private List<Order> queryDBSuccessOrder(String openid) {
        Wrapper<Order> wrapper = new QueryWrapper<Order>()
                .eq("appid", wxConfig.getAppID())
                .eq("openid", openid)
                .eq("mchid", wxConfig.getMchId())
                .eq("status", 2);
        log.info("缓存失效，开始从DB中获取支付订单数据 openid: [{}]", openid);
        return orderMapper.selectList(wrapper);
    }


}
