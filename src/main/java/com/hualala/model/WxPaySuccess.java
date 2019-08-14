package com.hualala.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;

/**
 * @author YuanChong
 * @create 2019-08-11 11:00
 * @desc
 */
@Getter
public class WxPaySuccess {

    @JacksonXmlProperty(localName = "return_code")
    private final String returnCode = "SUCCESS";

    @JacksonXmlProperty(localName = "return_msg")
    private final String returnMsg = "OK";

    public static final WxPaySuccess INSTANCE = new WxPaySuccess();

}
