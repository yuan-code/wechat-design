package com.hualala.controller;

import com.hualala.common.ResultCode;
import com.hualala.common.UserResolver;
import com.hualala.config.WXConfig;
import com.hualala.exception.BusinessException;
import com.hualala.model.User;
import com.hualala.model.WxPayRes;
import com.hualala.service.OrderService;
import com.hualala.util.ResultUtils;
import com.hualala.util.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author YuanChong
 * @create 2019-08-04 08:43
 * @desc
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WXConfig wxConfig;



    /**
     * 创建订单，发起预支付
     */
    @ResponseBody
    @RequestMapping(value = "/create")
    public Object create(@RequestParam("vipType") Integer vipType, @UserResolver User user) throws Exception {
        if(vipType == null || vipType == 0L) {
            throw new BusinessException(ResultCode.PARAMS_LOST.getCode(),"支付类型必传");
        }
        WxPayRes wxPayRes = orderService.create(vipType);

        Map<String,Object> result = new TreeMap<>();
        result.put("timeStamp",String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr",UUID.randomUUID().toString().replaceAll("-", ""));
        result.put("package","prepay_id=" + wxPayRes.getPrepayId());
        result.put("signType","MD5");
        result.put("appId",wxConfig.getAppID());

        String paySign = SignUtil.genarate(result);
        result.put("paySign",paySign);

        return ResultUtils.success(result);
    }


}
