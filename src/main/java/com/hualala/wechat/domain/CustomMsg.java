package com.hualala.wechat.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.NameFilter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author YuanChong
 * @create 2019-10-08 11:45
 * @desc
 */
@Data
public class CustomMsg {

    private String touser;

    private String msgtype;

    private Map<String, Object> params = new HashMap<>();

    @JSONField(serialize = false)
    private final CustomMsgFilter FILTER = new CustomMsgFilter();


    public CustomMsg(String touser, String msgtype) {
        this.touser = touser;
        this.msgtype = msgtype;
    }


    public CustomMsg buildImage(String mediaID) {
        params.put("media_id", mediaID);
        return this;
    }

    public String build() {
        return JSON.toJSONString(this,FILTER);
    }

    class CustomMsgFilter implements NameFilter {

        @Override
        public String process(Object object, String propertyName, Object propertyValue) {
            return Objects.equals("params",propertyName)?msgtype:propertyName;
        }
    }

}
