package com.hualala.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.common.ResultCode;
import com.hualala.config.WXConfig;
import com.hualala.exception.BusinessException;
import com.hualala.mapper.OrderMapper;
import com.hualala.model.Order;
import com.hualala.model.WXPayResult;
import com.hualala.model.WxPayReq;
import com.hualala.model.WxPayRes;
import com.hualala.util.BeanParse;
import com.hualala.util.MoneyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;

/**
 * @author YuanChong
 * @create 2019-08-04 08:43
 * @desc
 */
@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private WXService wxService;


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
     * 支付成功
     *
     * @param payResult
     */
    public void paySuccess(WXPayResult payResult) throws ParseException {
        Wrapper<Order> wrapper = new QueryWrapper<Order>()
                .eq("appid", payResult.getAppid())
                .eq("openid", payResult.getOpenid())
                .eq("mchid", payResult.getMchid())
                .eq("order_no",payResult.getOutTradeNo());
        Order order = orderMapper.selectOne(wrapper);
        if(order == null) {
            throw new BusinessException(ResultCode.PAY_ERROR);
        }
        order.validateMoney(MoneyUtil.Fen2Yuan(payResult.getCashFee()));
        order.calculateTime(payResult.getTimeEnd()).savePayResult(payResult);
        orderMapper.updateById(order);
    }
}
