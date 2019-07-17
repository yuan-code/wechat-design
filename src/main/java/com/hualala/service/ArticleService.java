package com.hualala.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.mapper.ArticleMapper;
import com.hualala.model.Article;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

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



    public Article articleCopy(String source) throws IOException {
        Connection connect = Jsoup.connect(source);
        // 得到Document对象
        Document document = connect.get();
        String head = document.head().toString();
        String content = document.getElementById("js_content").toString();
        String title = document.select("#activity-name").text();


        return null;
    }
}
