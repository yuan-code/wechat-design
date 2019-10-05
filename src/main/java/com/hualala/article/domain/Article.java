package com.hualala.article.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.hualala.util.HttpUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;

/**
 * <p>
 * 文章表
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "articleid", type = IdType.AUTO)
    private Long articleid;

    /**
     * 子版本的文章id
     */
    private Long pid;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 缩略图
     */
    private String thumbnail;


    /**
     * 用户ID
     */
    private Long userid;

    /**
     * OPENID
     */
    private String openid;


    /**
     * 创建日期
     */
    private Long createTime;

    /**
     * 最后更新日期
     */
    private Long modifyTime;

    /**
     * 来源于哪个用户id
     */
    private String sourceOpenid;

    /**
     * 来源于哪个用户id
     */
    private Long sourceUserid;

    /**
     * 文章来源
     */
    private String source;

    /**
     * 文章唯一性标识
     */
    private String checksum;


    public String resolveUrl() {
        return HttpUtils.getHostName() + "/article/detail/" + this.articleid;
    }

}
