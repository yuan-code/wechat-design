package com.hualala.account;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Maps;
import com.hualala.account.domain.Account;
import com.hualala.global.UserResolver;
import com.hualala.pay.OrderService;
import com.hualala.pay.domain.Order;
import com.hualala.user.UserService;
import com.hualala.user.domain.User;
import com.hualala.util.ResultUtils;
import com.hualala.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 用户账户明细记录表 前端控制器
 * </p>
 *
 * @author YuanChong
 * @since 2019-10-03
 */
@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private OrderService orderService;

    /**
     * 查询代理人数
     *
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping("/accountInfo")
    public Object members(@UserResolver User user) {
        QueryWrapper<Account> wrapper = new QueryWrapper<Account>().eq("openid", user.getOpenid()).eq("account_type", 2);
        List<Account> list = accountService.list(wrapper);
        BigDecimal sumAccount = list.stream().map(Account::getGoldNum).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal totalAccount = list.stream().map(Account::getGoldNum).filter(money -> money.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        List<Order> orderList = orderService.queryByAgent(user.getOpenid());
        long agentCount = orderList.stream().map(Order::getOpenid).distinct().count();
        HashMap<Object, Object> modelMap = Maps.newHashMap();
        modelMap.put("sumAccount",sumAccount);
        modelMap.put("totalAccount",totalAccount);
        modelMap.put("agentCount",agentCount);
        return ResultUtils.success(modelMap);
    }

    /**
     * 查询金币数量
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping("/coinCount")
    public Object coinCount(@UserResolver User user) {
        QueryWrapper<Account> wrapper = new QueryWrapper<Account>().eq("openid", user.getOpenid()).eq("account_type", 1);
        List<Account> list = accountService.list(wrapper);
        Long todayCoin = list.stream().map(Account::getCreateTime)
                .filter(time -> time >= TimeUtil.todayStartTime() && time <= TimeUtil.todayEndTime()).count();
        HashMap<Object, Object> modelMap = Maps.newHashMap();
        modelMap.put("sumCoin",list.size());
        modelMap.put("todayCoin",todayCoin.intValue());
        return ResultUtils.success(modelMap);
    }


}
