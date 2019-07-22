package com.hualala.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hualala.common.UserResolver;
import com.hualala.config.WXConfig;
import com.hualala.model.User;
import com.hualala.service.UserService;
import com.hualala.service.WXService;
import com.hualala.util.CacheUtils;
import com.hualala.util.MediaUtils;
import com.hualala.util.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;

/**
 * <p>
 * 公众号的用户信息 前端控制器
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-06
 */
@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private WXService wxService;



    /**
     * 更新用户签名或二维码
     *
     * @param params
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping("/passport/updateByID")
    public Object updateByID(User params, @UserResolver User user) throws Exception {
        if(StringUtils.isNotEmpty(params.getQrcode())){
            //上传图片
            InputStream inputStream = wxService.downloadMedia(user.getQrcode());
            String key = MediaUtils.uploadImage(inputStream);
            params.setQrcode(key);
        }
        Wrapper<User> wrapper = new UpdateWrapper<User>().eq("appid", wxConfig.getAppID()).eq("openid", user.getOpenid());
        userService.update(params,wrapper);
        //获取缓存的登陆用户
        String json = CacheUtils.get(user.getToken());
        User loginUser = JSON.parseObject(json, User.class);
        //把本次修改同步到缓存
        BeanUtil.copyProperties(params,loginUser, CopyOptions.create().setIgnoreNullValue(true));
        CacheUtils.set(user.getToken(), JSON.toJSONString(loginUser));
        return ResultUtils.success(params);
    }

}
