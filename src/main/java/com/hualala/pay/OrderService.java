package com.hualala.pay;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Preconditions;
import com.hualala.account.AccountService;
import com.hualala.account.domain.Account;
import com.hualala.pay.common.PayConfig;
import com.hualala.pay.common.RedisKey;
import com.hualala.pay.common.VipType;
import com.hualala.pay.domain.Order;
import com.hualala.pay.domain.WXPayResult;
import com.hualala.pay.domain.WxPayReq;
import com.hualala.pay.domain.WxPayRes;
import com.hualala.pay.util.MoneyUtil;
import com.hualala.user.domain.User;
import com.hualala.util.*;
import com.hualala.wechat.WXService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private PayConfig payConfig;

    @Autowired
    private WXService wxService;

    @Autowired
    private AccountService accountService;


    /**
     * 创建订单
     *
     * @param vipType
     */
    @Transactional(rollbackFor = Exception.class)
    public WxPayRes create(Integer vipType) throws Exception {
        Order order = new Order();
        order.setOrderType(vipType);
        order.setMchid(payConfig.getMchId());
        //生成订单号 计算金额 绑定用户 IP
        order.generateNo().computeMoney().findIP().setStatus(1).binding();
        //普通vip订单设置介绍人
        if (!Objects.equals(vipType, VipType.AGENT.getType())) {
            User user = CurrentUser.getUser();
            order.setSponsorOpenid(user.getSponsorOpenid());
            order.setSponsorUserid(user.getSponsorUserid());
        }
        orderMapper.insert(order);
        //微信支付订单
        WxPayReq wxPayOrder = new WxPayReq(order);
        wxPayOrder.buildSortMap().sign();
        String xml = BeanParse.beanToXMl(wxPayOrder);
        WxPayRes wxPayRes = wxService.payOrder(xml);
        return wxPayRes;
    }

    /**
     * 免费试用
     *
     * @param openid
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Order createFreeOrder(String openid) {
        List<Order> orders = this.successVipOrder(openid);
        if (orders.size() > 0) {
            throw new RuntimeException("非法请求");
        }
        Order order = new Order();
        Long currentDT = TimeUtil.currentDT();
        order.setOrderType(VipType.FREE.getType())
                .computeMoney().generateNo().calculateTime(currentDT).findIP().binding();
        order.setMchid(payConfig.getMchId());
        order.setCreateTime(currentDT);
        order.setActionTime(currentDT);
        order.setStatus(2);
        orderMapper.insert(order);
        return order;
    }


    /**
     * 支付成功 订单会被缓存 这里的缓存仅仅起到了缓存的作用 缓存失效会从DB重新查
     *
     * @param payResult
     */
    @Transactional(rollbackFor = Exception.class)
    public Order paySuccess(WXPayResult payResult) {
        Wrapper<Order> wrapper = new QueryWrapper<Order>()
                .eq("appid", payResult.getAppid())
                .eq("openid", payResult.getOpenid())
                .eq("mchid", payResult.getMchid())
                .eq("order_no", payResult.getOutTradeNo());
        Order order = orderMapper.selectOne(wrapper);
        Preconditions.checkNotNull(order, "非法请求");
        order.validateMoney(payResult.getFeeType(), MoneyUtil.Fen2Yuan(payResult.getCashFee()));
        //普通订单计算起止时间
        if (!Objects.equals(order.getOrderType(), VipType.AGENT.getType())) {
            //如果有重复支付，计算订单的下次起止时间
            Long beginTime = payResult.getTimeEnd();
            //查询vip结束时间
            Long vipEndTime = selectVipEndTime(payResult.getOpenid(), order.getOrderNo());
            if (vipEndTime != null && vipEndTime >= beginTime) {
                beginTime = TimeUtil.stepTime(vipEndTime, Calendar.SECOND, 1);
            }
            order.calculateTime(beginTime);
            //为代理加钱
            if (StringUtils.isNotEmpty(order.getSponsorOpenid())) {
                Account account = new Account();
                account.setAccountType(2);
                account.setCreateTime(TimeUtil.currentDT());
                account.setOpenid(order.getSponsorOpenid());
                account.setUserid(order.getSponsorUserid());
                account.setGoldNum(DecimalUtils.bigDiv(order.getCashFee(), BigDecimal.valueOf(2)));
                accountService.save(account);
            }
            //为自己加金币
            List<Order> orders = this.successOrder(order.getOpenid());
            Account account = new Account();
            account.setAccountType(1);
            account.setCreateTime(TimeUtil.currentDT());
            account.setOpenid(order.getOpenid());
            account.setUserid(order.getUserid());
            if(orders.size() > 0) {
                account.setGoldNum(DecimalUtils.bigDiv(order.getCashFee(), BigDecimal.valueOf(2)));
            }else {
                account.setGoldNum(order.getCashFee());
            }
            accountService.save(account);
        }
        order.savePayResult(payResult);
        orderMapper.updateById(order);
        return order;
    }


    /**
     * 查询vip结束时间
     *
     * @param openid
     * @return
     */
    public Long selectVipEndTime(String openid) {
        return selectVipEndTime(openid, null);
    }


    /**
     * 查询vip结束时间
     *
     * @param openid
     * @param excludeNo
     * @return
     */
    public Long selectVipEndTime(String openid, String excludeNo) {
        return orderMapper.selectVipEndTime(wxService.getAppID(), openid, excludeNo);
    }

    /**
     * 获取用户在当前时间生效的vip订单
     *
     * @param openid
     * @return
     */
    public Optional<Order> successAgentOrder(String openid) {
        List<Order> orderList = successOrder(openid);
        return orderList.stream().filter(order -> order.getOrderType() == 4).findAny();
    }


    public List<Order> queryByAgent(String openid) {
        Wrapper<Order> wrapper = new QueryWrapper<Order>()
                .eq("appid", wxService.getAppID())
                .eq("mchid", payConfig.getMchId())
                .eq("status", 2)
                .eq("sponsor_openid",openid);
        return orderMapper.selectList(wrapper);
    }



    public Optional<Order> currentVipOrder(String openid) {
        List<Order> orderList = successVipOrder(openid);
        Long currenTime = TimeUtil.currentDT();
        return orderList.stream().filter(order -> currenTime >= order.getBeginTime() && currenTime <= order.getEndTime()).findAny();
    }

    /**
     * 获取vip订单
     *
     * @param openid
     * @return
     */
    public List<Order> successVipOrder(String openid) {
        List<Order> orderList = successOrder(openid);
        orderList.removeIf(order -> order.getOrderType() == 4);
        return orderList;
    }

    /**
     * 获取支付成功的订单
     *
     * @param openid
     * @return
     */
    public List<Order> successOrder(String openid) {
        if (validateCacheOrder(openid)) {
            try {
                String redisKey = String.format(RedisKey.PAY_ORDER_KEY, wxService.getAppID(), payConfig.getMchId(), openid);
                Set<String> jsonSet = CacheUtils.zRangeRevertAll(redisKey);
                return jsonSet.stream().map(json -> JSON.parseObject(json, Order.class)).collect(Collectors.toList());
            } catch (Exception e) {
                //do nothing
                log.error("获取最后一次订单出现异常，可能是缓存挂了", e);
            }
        }
        List<Order> orderList = queryDBSuccessOrder(openid);
        orderList.stream().forEach(order -> cacheOrder(order));
        return orderList.stream().sorted(Comparator.comparing(Order::getEndTime).reversed()).collect(Collectors.toList());
    }


    /**
     * 缓存订单
     *
     * @param order
     */
    private void cacheOrder(Order order) {
        try {
            String redisKey = String.format(RedisKey.PAY_ORDER_KEY, wxService.getAppID(), payConfig.getMchId(), order.getOpenid());
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
                    .eq("appid", wxService.getAppID())
                    .eq("openid", openid)
                    .eq("mchid", payConfig.getMchId())
                    .eq("status", 2);
            Integer dbCount = orderMapper.selectCount(wrapper);
            String redisKey = String.format(RedisKey.PAY_ORDER_KEY, wxService.getAppID(), payConfig.getMchId(), openid);
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
                .eq("appid", wxService.getAppID())
                .eq("openid", openid)
                .eq("mchid", payConfig.getMchId())
                .eq("status", 2);
        log.info("开始从DB中获取支付成功订单数据 openid: [{}]", openid);
        return orderMapper.selectList(wrapper);
    }


}
