package com.hualala.wechat.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YuanChong
 * @create 2019-01-28 15:54
 * @desc 微信公众号推送消息
 *
 * {
 *     "touser":"OPENID",
 *     "template_id":"ngqIpbwh8bUfcSsECmogfXcV14J0tQlEpBO27izEYtY",
 *     "url":"http://weixin.qq.com/download",
 *     "miniprogram":{
 *         "appid":"xiaochengxuappid12345",
 *         "pagepath":"index?foo=bar"
 *     },
 *     "data":{
 *         "first":{
 *             "value":"恭喜你购买成功！",
 *             "color":"#173177"
 *         },
 *         "keyword1":{
 *             "value":"巧克力",
 *             "color":"#173177"
 *         },
 *         "keyword2":{
 *             "value":"39.8元",
 *             "color":"#173177"
 *         },
 *         "keyword3":{
 *             "value":"2014年9月22日",
 *             "color":"#173177"
 *         },
 *         "remark":{
 *             "value":"欢迎再次购买！",
 *             "color":"#173177"
 *         }
 *     }
 * }
 */
@Getter
public class TemplateMsg {
    /**
     * 用户OpenID
     */
    private String touser;
    /**
     * 模板消息ID
     */
    @Setter
    @JSONField(name = "template_id")
    private String templateID;
    /**
     * URL置空，在发送后，点模板消息进入一个空白页面（ios），或无法点击（android）。
     */
    private String url;
    /**
     * 跳小程序所需数据，不需跳小程序可不用传该数据
     */
    private Miniprogram miniprogram;
    /**
     * 模板内容字体颜色，不填默认为黑色
     */
    private String color;
    /**
     * 模板详细信息
     */
    private Map<String, Content> data = new HashMap<>();
    /**
     * 模板编码
     */
    private String templateCode;

    private TemplateMsg() {
    }

    public static Builder builder(String touser, String templateCode) {
        return new Builder(touser,templateCode);
    }

    @Getter
    static class Content {
        private String value;
        private String color;
    }

    @Getter
    static class Miniprogram {
        /**
         * 所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系，暂不支持小游戏）
         */
        private String appid;
        /**
         * 所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），暂不支持小游戏
         */
        private String pagepath;
    }


    public static class Builder {

        private TemplateMsg templateMsg = new TemplateMsg();


        private Builder(String touser, String templateCode) {
            templateMsg.touser = touser;
            templateMsg.templateCode = templateCode;
        }


        public Builder buildFirst(String value) {
            return buildColorFirst(value,null);
        }

        public Builder buildColorFirst(String value, String color) {
            Content content = new Content();
            content.value = value;
            content.color = color;
            templateMsg.data.put("first", content);
            return this;
        }


        public Builder buildKeyword(String... values) {
            for (int i = 1; i <= values.length; i++) {
                Content content = new Content();
                content.value = values[i - 1];
                String key = "keyword" + i;
                templateMsg.data.put(key, content);
            }
            return this;
        }


        public Builder buildRemark(String value) {
            return buildColorRemark(value,null);
        }

        public Builder buildColorRemark(String value, String color) {
            Content content = new Content();
            content.value = value;
            content.color = color;
            templateMsg.data.put("remark", content);
            return this;
        }

        public TemplateMsg build() {
            return templateMsg;
        }
    }
}
