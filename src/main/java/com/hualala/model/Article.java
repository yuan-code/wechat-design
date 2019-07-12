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
     * 样式+meta
     */
    private String head;

    /**
     * 用户ID
     */
    private Long userid;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建日期
     */
    private Long createTime;

    /**
     * 最后更新日期
     */
    private Long modifyTime;

    /**
     * 备注信息
     */
    private String remarks;

    /**
     * 文章来源
     */
    private String source;


}