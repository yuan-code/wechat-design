package com.hualala.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hualala.article.domain.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 文章表 Mapper 接口
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-08
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 随机取一条原创文章 效率较低 可优化
     *
     * @return
     */
    Article findAny();

}
