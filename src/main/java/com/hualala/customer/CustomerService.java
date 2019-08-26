package com.hualala.customer;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.customer.domain.Customer;
import com.hualala.user.domain.User;
import com.hualala.util.TimeUtil;
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

    /**
     * 增加关注量
     * @param author
     * @param user
     * @param articleid
     */
    public void addCustomer(User author, User user, Long articleid) {
        Wrapper<Customer> wrapper = new QueryWrapper<Customer>()
                .eq("author_openid", author.getOpenid())
                .eq("customer_openid", user.getOpenid())
                .eq("articleid", articleid);
        Customer customer = customerMapper.selectOne(wrapper);
        if(customer == null) {
            customer = new Customer();
            customer.setArticleid(articleid);
            customer.setAuthorOpenid(author.getOpenid());
            customer.setAuthorUserid(author.getUserid());
            customer.setCustomerOpenid(user.getOpenid());
            customer.setCustomerUserid(user.getUserid());
            customer.setClickCount(1L);
            customer.setSubscibeTime(TimeUtil.currentDT());
        }else {
            Long clickCount = customer.getClickCount() + 1;
            customer.setClickCount(clickCount);
        }
        this.saveOrUpdate(customer);
    }
}
