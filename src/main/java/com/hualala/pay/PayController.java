package com.hualala.pay;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Preconditions;
import com.hualala.pay.common.VipType;
import com.hualala.pay.domain.Order;
import com.hualala.pay.domain.WxPayRes;
import com.hualala.pay.util.SignUtil;
import com.hualala.global.UserResolver;
import com.hualala.user.UserService;
import com.hualala.user.domain.User;
import com.hualala.util.LockHelper;
import com.hualala.util.ResultUtils;
import com.hualala.util.TimeUtil;
import com.hualala.wechat.WXService;
import com.hualala.wechat.domain.TemplateMsg;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.*;

/**
 * @author YuanChong
 * @create 2019-08-24 21:24
 * @desc
 */
@Log4j2
@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WXService wxService;

    @Autowired
    private LockHelper lockHelper;

    @Autowired
    private UserService userService;

    /**
     * 创建订单，发起预支付
     */
    @ResponseBody
    @RequestMapping("/create")
    public Object create(@RequestParam("vipType") Integer vipType, @UserResolver User user) throws Exception {
        Preconditions.checkArgument(vipType != null && vipType > 0L, "支付类型必传");
        WxPayRes wxPayRes = orderService.create(vipType);
        Map<String, Object> result = new TreeMap<>();
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr", UUID.randomUUID().toString().replaceAll("-", ""));
        result.put("package", "prepay_id=" + wxPayRes.getPrepayId());
        result.put("signType", "MD5");
        result.put("appId", wxService.getAppID());

        String paySign = SignUtil.genarate(result);
        result.put("paySign", paySign);

        return ResultUtils.success(result);
    }

    /**
     * 创建一个免费订单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/free")
    public Object free(@UserResolver User user) throws Exception {
        //同步锁
        String lock = "createFreeOrder/" + user.getOpenid();
        Order order = lockHelper.doSync(lock, () -> orderService.createFreeOrder(user.getOpenid()));
        //异步发模板消息
        TemplateMsg templateMsg = TemplateMsg.builder(order.getOpenid(), wxService.getVipSuccessTemplateCode())
                .buildFirst("Hi，亲爱的会员，你已成功开通青山高创高级会员！")
                .buildKeyword(order.getOrderNo(), "试用会员", "0", "至" + TimeUtil.formatTime(order.getEndTime()))
                .buildRemark("如有任何疑问，可联系客服")
                .build();
        wxService.asynSendMsg(templateMsg);
        return ResultUtils.success();
    }


    /**
     * 跳转vip页面
     *
     * @return
     */
    @RequestMapping(value = "/vip", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object vipType(@UserResolver User user, ModelMap modelMap) {
        List<Order> orderList = orderService.successVipOrder(user.getOpenid());
        Arrays.stream(VipType.values())
                .filter(type -> orderList.size() == 0? true: !VipType.FREE.equals(type))
                .forEach(type -> modelMap.addAttribute(type.name(),type));
        return "pay/vip";
    }

    /**
     * 查询vip结束时间
     * @param user
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @RequestMapping("/vipEndTime")
    public Object vipEndTime(@UserResolver User user) throws ParseException {
        Long endTime = orderService.selectVipEndTime(user.getOpenid());
        String result = "";
        if (endTime != null && endTime != 0L) {
            result = TimeUtil.formatTime(endTime);
        }
        return ResultUtils.success(result);
    }

    /**
     * 查询当前用户的下级代理列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/agentOrder")
    public Object agentOrder(Long pageNo, Long pageSize, @UserResolver User user) {
        Page<Order> page = new Page<>(pageNo, pageSize);
        Wrapper<Order> wrapper = new UpdateWrapper<Order>().eq("appid", wxService.getAppID()).eq("sponsor_openid", user.getOpenid());
        IPage<Order> result = orderService.page(page, wrapper);
        for(Order order: result.getRecords()) {
            User payUser = userService.queryByOpenid(order.getOpenid());
            order.setNickName(payUser.getNickname());
        }
        return ResultUtils.success(result);
    }


}
