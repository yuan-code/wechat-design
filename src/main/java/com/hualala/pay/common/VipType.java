package com.hualala.pay.common;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;

/**
 * @author YuanChong
 * @create 2019-08-11 09:49
 * @desc
 */
public enum VipType {


    FREE(0, BigDecimal.ZERO, "青山高创免费试用三天", Calendar.DATE,3),
    ONE(1, BigDecimal.valueOf(28.9), "青山高创一个月会员", Calendar.MONTH,1),
    TWO(2, BigDecimal.valueOf(98.9), "青山高创六个月会员",  Calendar.MONTH,6),
    THREE(3, BigDecimal.valueOf(148.9), "青山高创一年会员",  Calendar.MONTH,12),
    AGENT(4, BigDecimal.valueOf(99), "青山高创会员代理",  null,null);

    private Integer type;
    private BigDecimal price;
    private String desc;
    private Integer calendarType;
    private Integer calendarCount;



    VipType(Integer type, BigDecimal price, String desc, Integer calendarType, Integer calendarCount) {
        this.type = type;
        this.price = price;
        this.desc = desc;
        this.calendarType = calendarType;
        this.calendarCount = calendarCount;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCalendarCount() {
        return calendarCount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getType() {
        return type;
    }

    public Integer getCalendarType() {
        return calendarType;
    }


    public static VipType resolveType(Integer type) {
        Optional<VipType> priceEnum = Arrays.stream(VipType.values()).filter(price -> price.type.equals(type)).findAny();
        if (!priceEnum.isPresent()) {
            throw new IllegalArgumentException("非法参数type");
        }
        return priceEnum.get();
    }


}
