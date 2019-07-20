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

    /**
     * 如果是收到的关注事件 saveOrUpdate
     *     1.判断user表里没有这个用户，则代表净增的关注，直接插入
     *     2.否则，可能是之前取关过。更新用户最新的头像等信息，更新关注状态=1
     *     PS：由于服务器不稳定，关注事件可能会丢失，需要做补救措施
     *
     * 如果是网页授权（这里做补救措施）
     *     1.查询用户当前的关注状态，如果是路人甲，设置关注状态=3，saveOrUpdate
     *     2.如果是已关注的用户，表里也没有数据，那么这种情况就是丢了数据
     * @param user
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public User saveUser(User user) {
        user.setAppid(wxConfig.getAppID());
        if(user.getSubscribeStatus() != null && user.getSubscribeStatus() == 1) {
            //这里是抓取到的关注的请求
            user.setSubscribeStatus(1);
            return saveOrUpdateNew(user);
        }
        //这里是用户授权网页 做一个补救措施 防止丢失了某条关注用户
        //先查询用户状态是否关注 如果是关注的并且表里还没有数据 那么就是丢的数据
        User status = wxService.userBaseInfo(user.getOpenid());
        if(status.getSubscribeStatus() == 0) {
            //路人甲
            user.setSubscribeStatus(3);
        }else {
            user.setSubscribeScene(status.getSubscribeScene());
            user.setSubscribeTime(status.getSubscribeTime());
            user.setSubscribeStatus(1);
        }
        return saveOrUpdateNew(user);
    }

    /**
     * saveOrUpdate
     *
     * @param user
     * @return 数据库里最新的数据
     */
    public User saveOrUpdateNew(User user) {
        if(user.getSubscribeTime() != null) {
            //转换微信给的时间戳到yyyyMMddHHmmss
            Long convertTime = TimeUtil.covertTimestamp(user.getSubscribeTime() * 1000);
            user.setSubscribeTime(convertTime);
        }
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
