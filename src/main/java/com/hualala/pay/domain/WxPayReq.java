package com.hualala.pay.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.hualala.pay.util.MoneyUtil;
import com.hualala.util.HttpUtils;
import com.hualala.pay.util.SignUtil;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

@Data
public class WxPayReq {

    private String appid;

    private String attach;

    private String body;

    private String detail;

    @JacksonXmlProperty(localName = "mch_id")
    private String mchId;

    @JacksonXmlProperty(localName = "nonce_str")
    private String nonceStr;

    @JacksonXmlProperty(localName = "notify_url")
    private String notifyUrl;

    private String openid;

    @JacksonXmlProperty(localName = "out_trade_no")
    private String outTradeNo;

    @JacksonXmlProperty(localName = "spbill_create_ip")
    private String spbillCreateIp;


    @JacksonXmlProperty(localName = "total_fee")
    private Integer totalFee;

    @JacksonXmlProperty(localName = "trade_type")
    private String tradeType;


    private String sign;

    private Map<String, Object> params;


    public WxPayReq(Order order) {
        setOutTradeNo(order.getOrderNo());
        setTotalFee(MoneyUtil.Yuan2Fen(order.getOrderAmount()));
        setBody(order.getOrderDesc());
        setOpenid(order.getOpenid());
        setTradeType("JSAPI");
        setAppid(order.getAppid());
        setMchId(order.getMchid());
        setNonceStr(UUID.randomUUID().toString().replaceAll("-", ""));
        setSpbillCreateIp(order.getClientip());
        setNotifyUrl(HttpUtils.getHostName() + "/wx/pay");
    }


    public WxPayReq buildSortMap() throws IllegalAccessException {
        Map<String, Object> params = new TreeMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(this);
            if (value != null) {
                String key = Optional.ofNullable(field.getAnnotation(JacksonXmlProperty.class)).map(anno -> anno.localName()).orElse(field.getName());
                params.put(key, value);
            }
        }
        this.params = params;
        return this;
    }

    /**
     * 签名
     *
     * @return
     */
    public WxPayReq sign() {
        this.sign = SignUtil.genarate(params);
        return this;
    }


}

