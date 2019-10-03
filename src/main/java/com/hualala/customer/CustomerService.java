package com.hualala.customer;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.account.AccountService;
import com.hualala.account.domain.Account;
import com.hualala.article.domain.Article;
import com.hualala.customer.domain.Customer;
import com.hualala.pay.OrderService;
import com.hualala.user.domain.User;
import com.hualala.util.CurrentUser;
import com.hualala.util.DecimalUtils;
import com.hualala.util.LockHelper;
import com.hualala.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

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

    @Autowired
    private AccountService accountService;

    @Autowired
    private OrderService orderService;


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
     * @param article
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addCustomer(User author, User user, Article article) {
        Wrapper<Customer> wrapper = new QueryWrapper<Customer>()
                .eq("author_openid", author.getOpenid())
                .eq("customer_openid", user.getOpenid())
                .eq("articleid", article.getArticleid());
        Customer customer = customerMapper.selectOne(wrapper);
        if(customer == null) {
            customer = new Customer();
            customer.setArticleid(article.getArticleid());
            customer.setAuthorOpenid(author.getOpenid());
            customer.setAuthorUserid(author.getUserid());
            customer.setCustomerOpenid(user.getOpenid());
            customer.setCustomerUserid(user.getUserid());
            customer.setClickCount(1L);
            customer.setSubscibeTime(TimeUtil.currentDT());
            //给作者加金币
            Account account = new Account();
            account.setAccountType(1);
            account.setCreateTime(TimeUtil.currentDT());
            account.setOpenid(author.getOpenid());
            account.setUserid(author.getUserid());
            int mul = orderService.currentVipOrder(author.getOpenid()).isPresent() ? 5 : 1;
            if(Objects.equals(author.getOpenid(),article.getSourceOpenid())) {
                account.setGoldNum(BigDecimal.valueOf(2 * mul));
            }else {
                account.setGoldNum(BigDecimal.valueOf(1 * mul));
            }
            accountService.save(account);
            //给代理加金币
            if(StringUtils.isNotEmpty(author.getSponsorOpenid())) {
                account.setGoldNum(BigDecimal.ONE);
                account.setOpenid(author.getSponsorOpenid());
                account.setUserid(author.getSponsorUserid());
                account.setId(null);
                accountService.save(account);
            }

        }else {
            Long clickCount = customer.getClickCount() + 1;
            customer.setClickCount(clickCount);
        }
        return this.saveOrUpdate(customer);
    }
}
