package com.hualala.controller;

import com.hualala.componet.NotifyFactory;
import com.hualala.config.WXConfig;
import com.hualala.util.XMLParse;
import com.hualala.weixin.mp.WXBizMsgCrypt;
import io.micrometer.core.instrument.util.IOUtils;
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
                           @RequestParam("signature") String msgSignature,
                           @RequestParam(value = "echostr",defaultValue = "") String echostr,
                           HttpServletRequest request) throws Exception {

        log.info("Msg接收到的POST请求：signature={}, timestamp={}, nonce={}, echostr={}",
                msgSignature, timestamp, nonce, echostr);

        ServletInputStream inputStream = request.getInputStream();
        String xmlData = IOUtils.toString(inputStream);
        Map<String, String> encryptMap = XMLParse.xmlToMap(xmlData);

        WXBizMsgCrypt pc = new WXBizMsgCrypt(wxConfig.getToken(), wxConfig.getEncodingAESKey(), wxConfig.getAppID());
        // 得到公众号传来的加密信息并解密,得到的是明文xml数据
        String decryptXml = pc.decrypt(encryptMap.get("Encrypt"));
        // 将xml数据转换为map
        Map<String, String> decryptMap = XMLParse.xmlToMap(decryptXml);
        //可以拿到的事件: 1 关注/取消关注事件  2:用户还未关注公众号 扫描带参数二维码事件 3: 用户已经关注公众号 扫描带参数二维码事件 ...等等
        //微信有重试机制  需要考虑去重
        NotifyFactory.WeChatNotify infoType = notifyFactory.createWeChatNotify(decryptMap.get("Event"));
        log.info("创建授权策略对象: [{}]",infoType.getClass().getSimpleName());
        //通知
        infoType.weChatNotify(decryptMap);
        return echostr;
    }
}
