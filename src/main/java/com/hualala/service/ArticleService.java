package com.hualala.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.mapper.ArticleMapper;
import com.hualala.model.Article;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文章表 服务类
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-08
 */
@Service
public class ArticleService extends ServiceImpl<ArticleMapper, Article> {

}
