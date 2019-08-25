package com.hualala.customer;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.customer.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 查询某个作者的文章的阅读量
     *
     * @param openid
     * @param articleid
     * @return
     */
    public Integer queryCustomerCount(String openid, Long articleid) {
        Wrapper<Customer> wrapper = new QueryWrapper<Customer>()
                .eq("author_openid", openid)
                .eq("articleid", articleid);
        return customerMapper.selectCount(wrapper);
    }

}
