package com.hualala.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.config.CosConfig;
import com.hualala.mapper.ArticleMapper;
import com.hualala.model.Article;
import com.hualala.util.HttpClientUtil;
import com.hualala.util.MediaUtils;
import com.hualala.util.TimeUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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


    @Autowired
    private ArticleMapper articleMapper;


    /**
     * 爬取单个公众号文章
     *
     * @param source
     * @return
     * @throws IOException
     */
    public Article articleCopy(String source) throws IOException {
        QueryWrapper<Article> wrapper = new QueryWrapper<Article>().eq("source", source);
        Article article = articleMapper.selectOne(wrapper);
        if (article != null) {
            return article;
        }
        Connection connect = Jsoup.connect(source);
        Document document = connect.get();
        String head = document.head().toString();
        //处理图片防盗链
        Element jsContent = document.getElementById("js_content");
        String content = replaceImage(jsContent).toString();
        String title = document.select("#activity-name").text();
        //获取JS变量
        Map<String, String> variableMap = scriptVariable(document);
        String summary = variableMap.get("msg_desc");
        String thumbnail = variableMap.get("msg_cdn_url");
        article = new Article();
        article.setHead(head);
        article.setContent(content);
        article.setTitle(title);
        article.setSummary(summary);
        article.setThumbnail(thumbnail);
        article.setSource(source);
        article.setCreateTime(TimeUtil.currentDT());
        articleMapper.insert(article);
        return article;
    }

    /**
     * 随机取一条原创文章
     *
     * @return
     */
    public Article findAny() {
        return articleMapper.findAny();
    }


    public Element replaceImage(Element element) throws IOException {
        Elements images = element.select("img");
        for(Element ele : images) {
            String imgUrl = ele.attr("data-src");
            byte[] bytes = HttpClientUtil.downLoadFromUrl(imgUrl);
            String newUrl = MediaUtils.uploadImage(bytes);
            ele.attr("src", newUrl);
        }
        return element;
    }


    public Map<String, String> scriptVariable(Document document) {
        Map<String, String> map = new HashMap<>();
        Elements elements = document.select("script[nonce]");
        for (Element element : elements) {
            String[] data = element.data().split("var");
            for (String variable : data) {
                if (variable.contains("=")) {
                    String[] kv = variable.split("=");
                    String key = kv[0].trim();
                    String value = kv[1].trim().replaceAll("\"", "").replaceAll(";", "");
                    map.put(key, value);
                }
            }
        }
        return map;
    }

}
