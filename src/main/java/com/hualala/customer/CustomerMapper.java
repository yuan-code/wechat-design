package com.hualala.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hualala.customer.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 客户关系表 Mapper 接口
 * </p>
 *
 * @author YuanChong
 * @since 2019-08-24
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

}
