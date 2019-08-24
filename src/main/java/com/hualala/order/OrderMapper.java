package com.hualala.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hualala.order.domain.Order;
import org.apache.ibatis.annotations.Mapper;

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



}
