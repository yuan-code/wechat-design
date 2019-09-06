package com.hualala.pay;

import com.google.common.collect.Lists;
import com.hualala.common.BusinessException;
import com.hualala.common.ResultCode;
import com.hualala.pay.common.VipTypeEnum;
import com.hualala.pay.domain.Order;
import com.hualala.pay.domain.WxPayRes;
import com.hualala.user.domain.User;
import com.hualala.util.LockHelper;
import com.hualala.util.ResultUtils;
import com.hualala.util.SignUtil;
import com.hualala.wechat.WXConfig;
import com.hualala.user.component.UserResolver;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author YuanChong
 * @create 2019-08-24 21:24
 * @desc
 */
@Log4j2
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private LockHelper lockHelper;

    /**
     * 创建订单，发起预支付
     */
    @ResponseBody
    @RequestMapping("/create")
    public Object create(@RequestParam("vipType") Integer vipType, @UserResolver User user) throws Exception {
        if (vipType == null || vipType == 0L) {
            throw new BusinessException(ResultCode.PARAMS_LOST.getCode(), "支付类型必传");
        }
        WxPayRes wxPayRes = orderService.create(vipType);

        Map<String, Object> result = new TreeMap<>();
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr", UUID.randomUUID().toString().replaceAll("-", ""));
        result.put("package", "prepay_id=" + wxPayRes.getPrepayId());
        result.put("signType", "MD5");
        result.put("appId", wxConfig.getAppID());

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
        lockHelper.doSync(lock, () -> orderService.createFreeOrder(user.getOpenid()));
        return ResultUtils.success();
    }


    /**
     * 查询可支付的vip类型
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/vip", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object vipType(@UserResolver User user, ModelMap modelMap) {
        List<Order> orderList = orderService.successOrder(user.getOpenid());
        Arrays.stream(VipTypeEnum.values())
                .filter(type -> orderList.size() == 0? true: !VipTypeEnum.FREE.equals(type))
                .forEach(type -> modelMap.addAttribute(type.name(),type));
        return "pay/vip";
    }


    @RequestMapping("/vipEndTime")
    public Object vipEndTime(@UserResolver User user) throws ParseException {
        Long endTime = orderService.selectVipEndTime(user.getOpenid());
        String result = "";
        if (endTime != null && endTime != 0L) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = sdf.parse(endTime.toString());
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            result = sdf2.format(date);
        }
        return ResultUtils.success(result);
    }

}
