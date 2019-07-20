package com.hualala.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.config.WXConfig;
import com.hualala.mapper.UserMapper;
import com.hualala.model.User;
import com.hualala.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 * 公众号的用户信息 服务实现类
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-06
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private WXService wxService;

    @Transactional(rollbackFor = Exception.class)
    public User saveUser(User user) {
        user.setAppid(wxConfig.getAppID());
        user.setSubscribeTime(TimeUtil.currentDT());
        if(user.getSubscribeStatus() != null && user.getSubscribeStatus() == 1) {
            //这里是抓取到的关注的请求
            return doSave(user);
        }
        //这里是用户授权网页 做一个补救措施 防止丢失了某条关注用户
        //先查询用户状态是否关注
        User status = wxService.userBaseInfo(user.getOpenid());
        if(status.getSubscribeStatus() == 0) {
            //路人甲
            user.setSubscribeStatus(3);
        }else {
            user.setSubscribeStatus(1);
        }
        return doSave(user);
    }


    private User doSave(User user) {
        Wrapper<User> wrapper = new QueryWrapper<User>().eq("appid", user.getAppid()).eq("openid", user.getOpenid());
        User dbUser = userMapper.selectOne(wrapper);
        if(dbUser == null) {
            //净增的人
            userMapper.insert(user);
        }else {
            userMapper.update(user,wrapper);
        }
        return userMapper.selectOne(wrapper);
    }

}
