package com.hualala.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 公众号的用户信息
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "userid", type = IdType.AUTO)
    private Long userid;

    /**
     * 公众号的ID
     */
    private String appid;

    /**
     * 用户的标识，对当前公众号唯一
     */
    private String openid;

    /**
     * 用户的昵称
     */
    private String nickname;

    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    private Integer sex;

    /**
     * 用户所在国家
     */
    private String country;

    /**
     * 用户所在省份
     */
    private String province;

    /**
     * 用户所在城市
     */
    private String city;

    /**
     * 用户的语言，简体中文为zh_CN
     */
    private String language;

    /**
     * 用户头像
     */
    private String headimgurl;

    /**
     * 用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移
     */
    private String subscribeScene;

    /**
     * 用户的关注状态 1-关注 2-取关
     */
    private Integer subscribeStatus;

    /**
     * 用户关注时间
     */
    private Long subscribeTime;

    /**
     * 用户取关时间
     */
    private Long unsubscribeTime;


}
