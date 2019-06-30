package com.hualala.controller;

import com.hualala.common.NotifyEnum;
import com.hualala.componet.NotifyFactory;
import com.hualala.componet.WeChatNotify;
import com.hualala.config.WXConfig;
import com.hualala.util.XMLParse;
import com.hualala.weixin.mp.WXBizMsgCrypt;
import io.micrometer.core.instrument.util.IOUtils;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @author YuanChong
 * @create 2019-06-26 21:51
 * @desc
 */
@Log4j2
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private NotifyFactory notifyFactory;

    /**
     * 公众号消息和事件推送
     *
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @param msgSignature 消息体签名
     * @param echostr
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/event")
    public Object official(@RequestParam("timestamp") String timestamp,
                           @RequestParam("nonce") String nonce,
                           @RequestParam(value = "msg_signature",required = false) String msgSignature,
                           @RequestParam(value = "echostr", required = false) String echostr,
                           @RequestBody(required = false) String postData) throws Exception {
        log.info("Msg接收到的POST请求: signature={}, timestamp={}, nonce={}, echostr={} postData={}", msgSignature, timestamp, nonce, echostr,postData);
        if(StringUtils.isEmpty(postData)) {
            return echostr;
        }
        WXBizMsgCrypt pc = wxConfig.getWxBizMsgCrypt();
        //签名校验 数据解密
        String decryptXml = pc.decryptMsg(msgSignature, timestamp, nonce, postData);
        Map<String, String> decryptMap = XMLParse.xmlToMap(decryptXml);
        //获取推送事件类型  可以拿到的事件: 1 关注/取消关注事件  2:扫描带参数二维码事件 3: 用户已经关注公众号 扫描带参数二维码事件 ...等等
        NotifyEnum notifyEnum = NotifyEnum.resolveEvent(decryptMap.get("MsgType"), decryptMap.get("Event"));
        WeChatNotify infoType = notifyFactory.loadWeChatNotify(notifyEnum);
        //执行具体的策略 得到给微信的响应信息 微信有重试机制  需要考虑幂等性
        String result = infoType.weChatNotify(decryptMap);
        log.info("Msg响应的POST结果: 授权策略对象: [{}] 解密后信息: [{}] 返回给微信的信息: [{}]", infoType.getClass().getSimpleName(), decryptMap, result);
        return result;
    }
}
