package com.hualala.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.mapper.CustomerMapper;
import com.hualala.model.Customer;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户关系表 服务实现类
 * </p>
 *
 * @author YuanChong
 * @since 2019-08-24
 */
@Service
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> {

}
