package com.hualala.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hualala.model.Order;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author YuanChong
 * @since 2019-08-04
 */
public interface OrderMapper extends BaseMapper<Order> {


    Order queryVipTime(@Param("appid") String appid, @Param("openid") String openid, @Param("mchid") String mchid);

}
