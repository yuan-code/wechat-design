package com.hualala.pay;

import com.hualala.common.BusinessException;
import com.hualala.common.ResultCode;
import com.hualala.pay.domain.WxPayRes;
import com.hualala.user.domain.User;
import com.hualala.util.ResultUtils;
import com.hualala.util.SignUtil;
import com.hualala.wechat.WXConfig;
import com.hualala.user.component.UserResolver;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

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
        result.put("nonceStr", UUID.randomUUID().toString().replaceAll("-", ""));
        result.put("package","prepay_id=" + wxPayRes.getPrepayId());
        result.put("signType","MD5");
        result.put("appId",wxConfig.getAppID());

        String paySign = SignUtil.genarate(result);
        result.put("paySign",paySign);

        return ResultUtils.success(result);
    }

}
