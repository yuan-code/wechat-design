package com.hualala.account.domain;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户账户明细记录表
 * </p>
 *
 * @author YuanChong
 * @since 2019-10-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("account")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private String openid;

    /**
     * 用户id
     */
    private Long userid;

    /**
     * 账户类型 1-金币 2-余额
     */
    private Integer accountType;

    /**
     * 数量 正数加 负数扣
     */
    private BigDecimal goldNum;

    /**
     * 创建时间
     */
    private Long createTime;


}
