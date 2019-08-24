package com.hualala.customer.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 客户关系表
 * </p>
 *
 * @author YuanChong
 * @since 2019-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户id
     */
    private String customerOpenid;

    /**
     * 客户id
     */
    private Long customerUserid;

    /**
     * 文章id
     */
    private Long articleid;

    /**
     * 作者id
     */
    private String authorOpenid;

    /**
     * 作者id
     */
    private Long authorUserid;


}
