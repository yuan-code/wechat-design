package com.hualala.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hualala.order.common.PriceEnum;
import com.hualala.common.ResultCode;
import com.hualala.common.BusinessException;
import com.hualala.order.util.MoneyUtil;
import com.hualala.pay.domain.WXPayResult;
import com.hualala.user.domain.User;
import com.hualala.util.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author YuanChong
 * @since 2019-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`order`")
public class Order implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "orderid", type = IdType.AUTO)
    private Long orderid;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 公众号的ID
     */
    private String appid;

    /**
     * 用户ID
     */
    private Long userid;

    /**
     * 商户号
     */
    private String mchid;

    /**
     * 用户的标识，对当前公众号唯一
     */
    private String openid;

    /**
     * 订单类型 1 2 3
     */
    private Integer orderType;

    /**
     * 生效开始时间
     */
    private Long beginTime;

    /**
     * 生效结束时间
     */
    private Long endTime;

    /**
     * 下单ip(客户端ip,从网关中获取)
     */
    private String clientip;

    /**
     * 支付银行类型
     */
    private String bankType;

    /**
     * 订单描述
     */
    private String orderDesc;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 支付金额
     */
    private BigDecimal cashFee;

    /**
     * 货币类型
     */
    private String feeType;

    /**
     * 微信支付订单号
     */
    private String transactionid;

    /**
     * 状态(支付中、支持成功、支付失败、退款中、退款成功)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 订单完成时间
     */
    private Long actionTime;


    /**
     * 生成订单号
     *
     * @return
     */
    public Order generateNo() {
        Long orderNo = SnowflakeID.generateId();
        this.orderNo = orderNo.toString();
        return this;
    }

    /**
     * 绑定订单用户
     *
     * @return
     */
    public Order binding() {
        User user = UserHolder.getUser();
        if (user == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        this.openid = user.getOpenid();
        this.appid = user.getAppid();
        this.userid = user.getUserid();
        this.createTime = TimeUtil.currentDT();
        this.status = 1;
        return this;
    }

    /**
     * 计算订单价钱
     *
     * @return
     */
    public Order computeMoney() {
        if (this.orderType == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        PriceEnum priceEnum = PriceEnum.resolveType(this.orderType);
        this.orderAmount = priceEnum.getPrice();
        this.orderDesc = priceEnum.getDesc();
        return this;
    }

    /**
     * 客户端IP
     *
     * @return
     */
    public Order findIP() {
        this.clientip = HttpUtils.getIpAddr();
        return this;
    }

    /**
     * 校验钱是否被串改
     *
     * @param money
     * @return
     */
    public Order validateMoney(BigDecimal money) {
        if (this.getOrderAmount().compareTo(money) != 0) {
            throw new BusinessException(ResultCode.PAY_ERROR);
        }
        return this;
    }

    /**
     * 保存支付结果
     *
     * @param payResult
     */
    public Order savePayResult(WXPayResult payResult) {
        this.bankType = payResult.getBankType();
        this.cashFee = MoneyUtil.Fen2Yuan(payResult.getCashFee());
        this.transactionid = payResult.getTransactionid();
        this.status = 2;
        this.actionTime = payResult.getTimeEnd();
        this.feeType = payResult.getFeeType();
        return this;
    }


    public Order calculateTime(Long time) throws ParseException {
        this.beginTime = time;
        PriceEnum priceEnum = PriceEnum.resolveType(this.getOrderType());
        this.endTime = TimeUtil.stepTime(time, Calendar.MONTH, priceEnum.getMonth());
        return this;
    }
}
