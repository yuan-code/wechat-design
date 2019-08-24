package com.hualala.controller;

import com.hualala.common.NotifyEnum;
import com.hualala.common.WXConstant;
import com.hualala.componet.NotifyFactory;
import com.hualala.componet.WechatNotify;
import com.hualala.config.WXConfig;
import com.hualala.model.WXPayResult;
import com.hualala.model.WxPaySuccess;
import com.hualala.service.OrderService;
import com.hualala.util.BeanParse;
import com.hualala.weixin.mp.WXBizMsgCrypt;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author YuanChong
 * @create 2019-06-26 21:51
 * @desc
 */
@Log4j2
@RestController
@RequestMapping("/wx")
public class WXController {


    @Autowired
    private NotifyFactory notifyFactory;

    @Autowired
    private OrderService orderService;

    /**
     * 公众号消息和事件推送
     *
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @param msgSignature 消息体签名
     * @param echostr      初次接入配置所需
     * @param postData     消息体
     * @return
     */
    @ResponseBody
    @RequestMapping("/event")
    public Object official(@RequestParam(value = "timestamp", required = false) String timestamp,
                           @RequestParam(value = "nonce", required = false) String nonce,
                           @RequestParam(value = "msg_signature", required = false) String msgSignature,
                           @RequestParam(value = "echostr", required = false) String echostr,
                           @RequestBody(required = false) String postData) throws Exception {
        log.info("Msg接收到的POST请求: signature={}, timestamp={}, nonce={}, echostr={} postData={}", msgSignature, timestamp, nonce, echostr, postData);
        if (StringUtils.isEmpty(postData)) {
            return echostr;
        }
        WXBizMsgCrypt pc = WXConfig.WX_BIZ_MSG_CRYPT;
        //签名校验 数据解密
        String decryptXml = pc.decryptMsg(msgSignature, timestamp, nonce, postData);
        Map<String, String> decryptMap = BeanParse.xmlToMap(decryptXml);
        //获取推送事件类型  可以拿到的事件: 1 关注/取消关注事件  2:扫描带参数二维码事件 3: 用户已经关注公众号 扫描带参数二维码事件 ...等等
        NotifyEnum notifyEnum = NotifyEnum.resolveEvent(decryptMap.get("MsgType"), decryptMap.get("Event"));
        WechatNotify infoType = notifyFactory.loadWeChatNotify(notifyEnum);
        //执行具体的策略 得到给微信的响应信息 微信有重试机制  需要考虑幂等性
        String result = infoType.wechatNotify(decryptMap);
        log.info("Msg响应的POST结果: 授权策略对象: [{}] 解密后信息: [{}] 返回给微信的信息: [{}]", infoType.getClass().getSimpleName(), decryptMap, result);
        return result;
    }


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
