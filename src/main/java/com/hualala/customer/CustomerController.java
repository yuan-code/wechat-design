package com.hualala.customer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hualala.customer.domain.Customer;
import com.hualala.user.UserService;
import com.hualala.user.domain.User;
import com.hualala.util.ResultUtils;
import com.hualala.util.TimeUtil;
import com.hualala.global.UserResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author YuanChong
 * @create 2019-08-24 20:36
 * @desc
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {


    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @RequestMapping("/customer")
    public String customer(@UserResolver User user, ModelMap modelMap) {
        QueryWrapper<Customer> wrapper = new QueryWrapper<Customer>().eq("author_openid", user.getOpenid()).groupBy("customer_openid");
        int allCount = customerService.count(wrapper);
        wrapper.between("subscibe_time", TimeUtil.todayStartTime(), TimeUtil.todayEndTime());
        int todayCount = customerService.count(wrapper);
        modelMap.put("allCount", allCount);
        modelMap.put("todayCount", todayCount);
        return "customer/customer";
    }

    @ResponseBody
    @RequestMapping("/list")
    public Object list(Long pageNo, Long pageSize, @UserResolver User user) {
        Page<Customer> page = new Page<>(pageNo, pageSize);
        QueryWrapper<Customer> wrapper = new QueryWrapper<Customer>().eq("author_openid", user.getOpenid()).orderByDesc("subscibe_time");
        IPage<Customer> result = customerService.page(page, wrapper);
        Map<Long, List<Customer>> relationMap = result.getRecords().stream().collect(Collectors.groupingBy(Customer::getCustomerUserid));
        if(relationMap.size() > 0) {
            Collection<User> customerList = userService.listByIds(relationMap.keySet());
            Map<Long, User> customerMap = customerList.stream().collect(Collectors.toMap(User::getUserid, Function.identity()));
            //这行代码就是为每一个关系赋值上客户的用户信息
            relationMap.entrySet().stream().forEach(entry -> Optional.ofNullable(customerMap.get(entry.getKey())).ifPresent(customer -> entry.getValue().stream().forEach(relation -> relation.setCustomerUser(customer))));
        }
        return ResultUtils.success(result);
    }


}
