package com.hualala.util;

import com.hualala.config.WXConfig;
import com.hualala.weixin.mp.AesException;
import com.hualala.weixin.mp.WXBizMsgCrypt;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author YuanChong
 * @create 2019-07-06 13:57
 * @desc 给微信回复信息的工具
 */
@Log4j2
public class WXReply {

    private String from;
    private String to;

    public WXReply(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /**
     * 回复文本
     *
     * @param msg
     * @return
     * @throws IOException
     * @throws AesException
     */
    public String replyMsg(String msg) throws IOException, AesException {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("FromUserName", from);
        resultMap.put("ToUserName", to);
        resultMap.put("MsgType", "text");
        resultMap.put("Content", msg);
        return encryptMsg(resultMap);
    }

    /**
     * 回复图片
     *
     * @param mediaId
     * @return
     * @throws IOException
     * @throws AesException
     */
    public String replyImage(String mediaId) throws IOException, AesException {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("FromUserName", from);
        resultMap.put("ToUserName", to);
        resultMap.put("MsgType", "image");
        Map<String, String> image = new HashMap<>();
        image.put("MediaId", mediaId);
        resultMap.put("Image", image);
        return encryptMsg(resultMap);
    }

    /**
     * 回复一条图文
     *
     * @param title
     * @param desc
     * @param picUrl
     * @param url
     * @return
     * @throws IOException
     * @throws AesException
     */
    public String replyNews(String title, String desc, String picUrl, String url) throws IOException, AesException {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("FromUserName", from);
        resultMap.put("ToUserName", to);
        resultMap.put("MsgType", "news");
        resultMap.put("ArticleCount", "1");
        Map<String, Object> articles = new HashMap<>();
        Map<String, Object> item = new HashMap<>();
        item.put("Title", title);
        item.put("Description", desc);
        item.put("PicUrl", picUrl);
        item.put("Url", url);
        articles.put("item", item);
        resultMap.put("Articles", articles);

        return encryptMsg(resultMap);

    }

    /**
     * 加密回复信息
     *
     * @param params
     * @return
     * @throws IOException
     * @throws AesException
     */
    private String encryptMsg(Map<String, Object> params) throws IOException, AesException {
        String timeStamp = Long.toString(System.currentTimeMillis());
        params.put("CreateTime", timeStamp);
        String replyMsg = XMLParse.mapToXml(params);
        WXBizMsgCrypt pc = WXConfig.wxBizMsgCrypt();
        //微信使用时间戳加随机数的方式来防止攻击(如果两次请求的时间戳+随机数都相同)
        String result = pc.encryptMsg(replyMsg, timeStamp, UUID.randomUUID().toString());
        return result;

    }


}
