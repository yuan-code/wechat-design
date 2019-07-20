package com.hualala.mapper;

import com.hualala.model.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 文章表 Mapper 接口
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-08
 */
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 随机取一条原创文章 效率较低 可优化
     *
     * @return
     */
    Article findAny();

}
