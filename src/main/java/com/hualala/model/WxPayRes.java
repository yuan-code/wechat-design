package com.hualala.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * @author YuanChong
 * @create 2019-08-11 11:00
 * @desc
 */
@Data
public class WxPayRes {

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
    private String mchId;

    @JacksonXmlProperty(localName = "device_info")
    private String deviceInfo;

    @JacksonXmlProperty(localName = "nonce_str")
    private String nonceStr;

    @JacksonXmlProperty(localName = "sign")
    private String sign;

    @JacksonXmlProperty(localName = "result_code")
    private String resultCode;

    @JacksonXmlProperty(localName = "err_code")
    private String errCode;

    @JacksonXmlProperty(localName = "err_code_des")
    private String errCodeDes;

    /**
     * 以下字段在return_code 和result_code都为SUCCESS的时候有返回.
     */
    @JacksonXmlProperty(localName = "trade_type")
    private String tradeType;

    @JacksonXmlProperty(localName = "prepay_id")
    private String prepayId;

    @JacksonXmlProperty(localName = "code_url")
    private String codeUrl;



}
