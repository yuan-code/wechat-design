package com.hualala.pay.common;

import com.hualala.common.ResultCode;
import com.hualala.common.BusinessException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author YuanChong
 * @create 2019-08-11 09:49
 * @desc
 */
public enum PriceEnum {


    ONE(1, BigDecimal.valueOf(0.01), "第一档订单测试", 1),
    TWO(2, BigDecimal.valueOf(0.02), "第二档订单测试", 6),
    THREE(3, BigDecimal.valueOf(0.03), "第三档订单测试", 12);

    private Integer type;
    private BigDecimal price;
    private String desc;
    private Integer month;


    PriceEnum(Integer type, BigDecimal price, String desc, Integer month) {
        this.type = type;
        this.price = price;
        this.desc = desc;
        this.month = month;
    }

    public Integer getMonth() {
        return month;
    }

    public String getDesc() {
        return desc;
    }

    public BigDecimal getPrice() {
        return price;
    }


    public static PriceEnum resolveType(Integer type) {
        Optional<PriceEnum> priceEnum = Arrays.stream(PriceEnum.values()).filter(price -> price.type.equals(type)).findAny();
        if (!priceEnum.isPresent()) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        return priceEnum.get();
    }


}
