package com.hualala.user;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.pay.OrderService;
import com.hualala.pay.domain.Order;
import com.hualala.user.common.Constant;
import com.hualala.user.domain.User;
import com.hualala.util.CacheUtils;
import com.hualala.util.CurrentUser;
import com.hualala.util.TimeUtil;
import com.hualala.wechat.WXService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * <p>
 * 公众号的用户信息 服务实现类
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-06
 */
@Log4j2
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WXService wxService;

    @Autowired
    private OrderService orderService;


    public List<User> queryByAgent(String openid) {
        Wrapper<User> wrapper = new QueryWrapper<User>().eq("appid", wxService.getAppID()).eq("sponsorOpenid", openid);
        return userMapper.selectList(wrapper);
    }

    /**
     * 推荐用户
     *
     * @param sponsorOpenid 介绍人
     * @param openID  目标人
     */
    @Transactional(rollbackFor = Exception.class)
    public int recommend(String sponsorOpenid, String openID) {
        Optional<Order> order = orderService.successAgentOrder(sponsorOpenid);
        if(!order.isPresent()) {
            //先判断介绍人是不是代理
            log.warn("sponsor:{} openid:{} 介绍人不是代理，推荐的用户无效",sponsorOpenid,openID);
            return 0;
        }
        User user = this.queryByOpenid(openID);
        if(StringUtils.isNotEmpty(user.getSponsorOpenid())) {
            //目标已经被代理过 不做修改
            log.warn("sponsor:{} openid:{} 推荐的用户已经被%s代理过，推荐无效",sponsorOpenid,openID,user.getSponsorOpenid());
            return 0;
        }
        User sponsor = this.queryByOpenid(sponsorOpenid);
        user.setSponsorOpenid(sponsor.getOpenid());
        user.setSponsorUserid(sponsor.getUserid());
        user.setSponsorTime(TimeUtil.currentDT());
        return userMapper.updateById(user);
    }


    /**
     * 如果是收到的关注事件 saveOrUpdate
     * 1.判断user表里没有这个用户，则代表净增的关注，直接插入
     * 2.否则，可能是之前取关过。更新用户最新的头像等信息，更新关注状态=1
     * PS：由于服务器不稳定，关注事件可能会丢失，需要做补救措施
     * <p>
     * 如果是网页授权（这里做补救措施）
     * 1.查询用户当前的关注状态，如果是路人甲，设置关注状态=3，saveOrUpdate
     * 2.如果是已关注的用户，表里也没有数据，那么这种情况就是丢了数据
     *
     * @param user
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public User saveUser(User user) {
        user.setAppid(wxService.getAppID());
        if (user.getSubscribeStatus() != null && user.getSubscribeStatus() == 1) {
            //这里是抓取到的关注的请求
            user.setSubscribeStatus(1);
            return saveOrUpdateNew(user);
        }
        //这里是用户授权网页 做一个补救措施 防止丢失了某条关注用户
        //先查询用户状态是否关注 如果是关注的并且表里还没有数据 那么就是丢的数据
        User status = wxService.userBaseInfo(user.getOpenid());
        if (status.getSubscribeStatus() == 0) {
            //路人甲
            user.setSubscribeStatus(3);
        } else {
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
    @Transactional(rollbackFor = Exception.class)
    public User saveOrUpdateNew(User user) {
        if (user.getSubscribeTime() != null) {
            //转换微信给的时间戳到yyyyMMddHHmmss
            Long convertTime = TimeUtil.covertTimestamp(user.getSubscribeTime() * 1000);
            user.setSubscribeTime(convertTime);
        }
        Wrapper<User> wrapper = new QueryWrapper<User>().eq("appid", user.getAppid()).eq("openid", user.getOpenid());
        User dbUser = queryByOpenid(user.getOpenid());
        if (dbUser == null) {
            //净增的人
            userMapper.insert(user);
        } else {
            //昵称不改
            user.setNickname(null);
            userMapper.update(user, wrapper);
        }
        return userMapper.selectOne(wrapper);
    }

    public User queryByOpenid(String openid) {
        Wrapper<User> wrapper = new QueryWrapper<User>().eq("appid", wxService.getAppID()).eq("openid", openid);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 添加session
     *
     * @param user
     */
    public String addSession(User user) {
        //返回给前端cookie
        //cookie内的token一小时过期
        String cookieKey = generateCookieKey(user.getOpenid());
        if (CacheUtils.exists(cookieKey)) {
            CacheUtils.expire(cookieKey, Constant.SESSION_EXPIRE_SECONDS);
        } else {
            CacheUtils.set(cookieKey, JSON.toJSONString(user), Constant.SESSION_EXPIRE_SECONDS);
        }
        return cookieKey;
    }


    public User vipAuth(User user) {
        List<Order> orderList = orderService.successOrder(user.getOpenid());
        Long currentTime = TimeUtil.currentDT();
        orderList.stream().filter(order -> currentTime >= order.getBeginTime() && currentTime <= order.getEndTime()).findAny().ifPresent(order -> user.setAvailable(true));
        orderList.stream().filter(order -> order.getOrderType() == 4).findAny().ifPresent(order -> user.setAgent(true));
        return user;
    }


    /**
     * 删除cookie
     *
     * @param openid
     */
    public void deleteSession(String openid) {
        String cookieKey = generateCookieKey(openid);
        CacheUtils.del(cookieKey);
        User user = queryByOpenid(openid);
        CurrentUser.setUser(user);
    }

    /**
     * token 认证
     *
     * @param token
     * @return
     */
    public User tokenAuth(String token) {
        String jsonUser = CacheUtils.get(token);
        if (StringUtils.isNotEmpty(jsonUser)) {
            User user = JSON.parseObject(jsonUser, User.class);
            CacheUtils.expire(token, Constant.SESSION_EXPIRE_SECONDS);
            //判断用户是否是有效的付费用户
            this.vipAuth(user);
            return user;
        }
        return null;
    }


    private String generateCookieKey(String openid) {
        return DigestUtils.md5Hex(wxService.getAppID() + openid);
    }
}
