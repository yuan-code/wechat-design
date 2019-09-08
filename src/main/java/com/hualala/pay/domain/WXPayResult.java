package com.hualala.pay.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.hualala.util.SignUtil;
import lombok.Data;

/**
 * @author YuanChong
 * @create 2019-08-13 16:53
 * @desc
 */
@Data
public class WXPayResult {


    @JacksonXmlProperty(localName = "return_code")
    private String returnCode;

    @JacksonXmlProperty(localName = "return_msg")
    private String returnMsg;

    /**
     * 以下字段在return_code为SUCCESS的时候有返回.
     */
    @JacksonXmlProperty(localName = "appid")
    private String appid;

    @JacksonXmlProperty(localName = "mch_id")
    private String mchid;

    @JacksonXmlProperty(localName = "device_info")
    private String deviceInfo;

    @JacksonXmlProperty(localName = "nonce_str")
    private String nonceStr;

    @JacksonXmlProperty(localName = "sign")
    private String sign;

    @JacksonXmlProperty(localName = "sign_type")
    private String signType;

    @JacksonXmlProperty(localName = "result_code")
    private String resultCode;

    @JacksonXmlProperty(localName = "err_code")
    private String errCode;

    @JacksonXmlProperty(localName = "err_code_des")
    private String errCodeDes;

    @JacksonXmlProperty(localName = "openid")
    private String openid;

    @JacksonXmlProperty(localName = "is_subscribe")
    private String isSubscribe;

    @JacksonXmlProperty(localName = "bank_type")
    private String bankType;

    @JacksonXmlProperty(localName = "total_fee")
    private Integer totalFee;

    @JacksonXmlProperty(localName = "cash_fee")
    private Integer cashFee;

    @JacksonXmlProperty(localName = "cash_fee_type")
    private String cashFeeType;

    @JacksonXmlProperty(localName = "settlement_total_fee")
    private Integer settlementTotalFee;

    @JacksonXmlProperty(localName = "fee_type")
    private String feeType;

    @JacksonXmlProperty(localName = "transaction_id")
    private String transactionid;

    @JacksonXmlProperty(localName = "out_trade_no")
    private String outTradeNo;

    @JacksonXmlProperty(localName = "time_end")
    private Long timeEnd;

    @JacksonXmlProperty(localName = "attach")
    private String attach;

    @JacksonXmlProperty(localName = "trade_type")
    private String tradeType;


    public WXPayResult baseValidate() throws IllegalAccessException {
        String sign = SignUtil.genarate(this);
        if(!sign.equals(this.getSign())) {
            throw new RuntimeException("签名失败");
        }
        if(!this.getTotalFee().equals(this.getCashFee())) {
            throw new RuntimeException("非法请求");
        }
        return this;
    }

}
