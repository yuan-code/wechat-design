package com.hualala.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章表
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-08
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
    private Integer articleid;

    /**
     * 子版本的文章id
     */
    private Integer pid;

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
     * 样式+meta
     */
    private String head;

    /**
     * 用户ID
     */
    private Integer userid;

    /**
     * 排序编号
     */
    private Integer sortNumber;

    /**
     * 状态
     */
    private String status;

    /**
     * 评论状态，默认允许评论
     */
    private Boolean commentStatus;

    /**
     * 评论总数
     */
    private Integer commentCount;

    /**
     * 最后评论时间
     */
    private Long commentTime;

    /**
     * 访问量
     */
    private Integer viewCount;

    /**
     * 创建日期
     */
    private Long created;

    /**
     * 最后更新日期
     */
    private Long modified;

    /**
     * 标识，通常用于对某几篇文章进行标识，从而实现单独查询
     */
    private String flag;

    /**
     * 备注信息
     */
    private String remarks;

    /**
     *  文章来源
     */
    private String source;

}
