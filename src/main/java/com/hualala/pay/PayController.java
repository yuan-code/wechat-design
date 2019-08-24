package com.hualala.pay;

import com.hualala.pay.domain.WXPayResult;
import com.hualala.pay.domain.WxPaySuccess;
import com.hualala.order.OrderService;
import com.hualala.wechat.common.WXConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuanChong
 * @create 2019-08-24 21:24
 * @desc
 */
@Log4j2
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private OrderService orderService;


    /**
     * 微信支付回调
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pay", produces = MediaType.APPLICATION_XML_VALUE)
    public Object pay(@RequestBody WXPayResult payResult) throws Exception {
        log.info("Msg接收到微信支付回调请求: payResult={}", payResult);
        if (!payResult.getReturnCode().equals(WXConstant.SUCCESS)) {
            return WxPaySuccess.INSTANCE;
        }
        if (!payResult.getResultCode().equals(WXConstant.SUCCESS)) {
            return WxPaySuccess.INSTANCE;
        }
        //基础校验 签名 金额
        payResult.baseValidate();
        orderService.paySuccess(payResult);
        return WxPaySuccess.INSTANCE;
    }
}
