package com.hualala.wechat;

import com.alibaba.fastjson.JSON;
import com.hualala.mail.MailService;
import com.hualala.pay.OrderService;
import com.hualala.pay.domain.Order;
import com.hualala.pay.domain.WXPayResult;
import com.hualala.pay.domain.WxPaySuccess;
import com.hualala.util.TimeUtil;
import com.hualala.wechat.common.NotifyEnum;
import com.hualala.wechat.common.TemplateCode;
import com.hualala.wechat.common.WXConstant;
import com.hualala.wechat.component.NotifyFactory;
import com.hualala.wechat.component.WXConfig;
import com.hualala.wechat.component.WechatNotify;
import com.hualala.util.BeanParse;
import com.hualala.wechat.domain.TemplateMsg;
import com.hualala.weixin.mp.WXBizMsgCrypt;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Autowired
    private MailService mailService;

    @Value("#{'${spring.mail.toUser}'.split(',')}")
    private List<String> mailUser;

    @Autowired
    private WXService wxService;


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
        Order order = orderService.paySuccess(payResult);

        //异步发模板消息
        TemplateMsg templateMsg = TemplateMsg.builder(order.getOpenid(), TemplateCode.BECOME_VIP_SUCCESS)
                .buildFirst("Hi，亲爱的会员，你已成功开通青山高创高级会员！")
                .buildKeyword(order.getOrderNo(), "高级会员", order.getCashFee().toString(), "至" + TimeUtil.formatTime(order.getEndTime()))
                .buildRemark("如有任何疑问，可联系客服")
                .build();
        wxService.asynSendMsg(templateMsg);

        //异步发邮件
        String msg = "有人购买会员了，openID=%s 支付金额=%s元 order信息:%s";
        String content = String.format(msg,order.getOpenid(),order.getCashFee(), JSON.toJSONString(order));
        mailUser.stream().forEach(toUser -> mailService.sendMail(toUser,"青山高创公众号新增一个会员订单",content));
        return WxPaySuccess.INSTANCE;
    }
}
