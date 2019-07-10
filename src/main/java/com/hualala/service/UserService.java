package com.hualala.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        user.setSubscribeTime(TimeUtil.currentDT());
        Wrapper<User> wrapper = new QueryWrapper<User>().eq("appid", user.getAppid()).eq("openid", user.getOpenid());
        Integer count = userMapper.selectCount(wrapper);
        if(count == 0) {
            //净增的人
            userMapper.insert(user);
        }else {
            //之前关注过 取关后又重新关注了
            user.setSubscribeStatus(1);
            user.setUnsubscribeTime(0L);
            userMapper.update(user,wrapper);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserBaseInfo(User user) {
        Wrapper<User> wrapper = new UpdateWrapper<User>().eq("appid", user.getAppid()).eq("openid", user.getOpenid());
        userMapper.update(user,wrapper);
    }
}
