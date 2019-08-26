package com.hualala.pay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hualala.pay.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author YuanChong
 * @since 2019-08-04
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询vip结束时间
     * @param appid
     * @param openid
     * @param excludeNo
     * @return
     */
    Long selectVipEndTime(@Param("appid") String appid, @Param("openid") String openid, @Param("excludeNo") String excludeNo);
}
