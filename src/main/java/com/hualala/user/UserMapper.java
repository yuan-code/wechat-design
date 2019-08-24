package com.hualala.user;

import com.hualala.user.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 公众号的用户信息 Mapper 接口
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
