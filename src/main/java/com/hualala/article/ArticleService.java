package com.hualala.article;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.article.domain.Article;
import com.hualala.cos.MediaUtils;
import com.hualala.util.HttpClientUtil;
import com.hualala.util.TimeUtil;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * 文章表 服务类
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-08
 */
@Log4j2
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
    @Transactional(rollbackFor = Exception.class)
    public Article articleCopy(String source) {
        QueryWrapper<Article> wrapper = new QueryWrapper<Article>().eq("source", source).eq("pid", 0);
        //这行代码块是同步的
        Article article = articleMapper.selectOne(wrapper);
        if (article != null) {
            return article;
        }
        Document document = connetUrl(source);
        //处理图片防盗链
        Element jsContent = document.getElementById("js_content");
        // 去掉所有超链接
        Elements elements = document.getElementsByTag("a");
        for (Element element : elements) {
            element.attr("href", "");
            element.text("");
        }
        //移除最后的图片和文本
        Elements elementsImg = document.getElementsByTag("img");
        if(elementsImg != null && !elementsImg.isEmpty()) {
            elements.last().remove();
        }
        elementsImg = document.getElementsByTag("p");
        for(int i = elementsImg.size() - 1; i >= 0; i--) {
            Element element = elementsImg.get(i);
            if(element.hasText()) {
                element.remove();
                break;
            }
        }
        String content = replaceImage(jsContent).toString();
        String title = document.select("#activity-name").text();
        //获取JS变量
        Map<String, String> variableMap = scriptVariable(document);
        String summary = variableMap.get("msg_desc");
        String thumbnail = variableMap.get("msg_cdn_url");
        byte[] bytes = HttpClientUtil.downLoadFromUrl(thumbnail);
        thumbnail = MediaUtils.uploadImage(bytes);
        article = new Article();
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



    private Document connetUrl(String source) {
        try {
            Connection connect = Jsoup.connect(source);
            return connect.get();
        } catch (Exception e) {
            log.error("复制文章url: {} 地址连接失败", source, e);
            throw new RuntimeException("文章地址不合法");
        }
    }



    public Element replaceImage(Element element) {
        Elements stylesEle = element.getElementsByAttribute("style");

        for(Element ele : stylesEle) {
            String style = ele.attr("style");
            Optional<String> url = Arrays.stream(style.split(";"))
                    .filter(s -> s.contains("background-image") && s.contains("\""))
                    .map(s -> s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\"")))
                    .findAny();
            if(url.isPresent()) {
                String newUrl = MediaUtils.uploadImage(url.get());
                String replace = style.replace(url.get(), newUrl);
                ele.attr("style",replace);
            }
        }
        Elements images = element.select("img");
        for (Element ele : images) {
            String imgUrl = ele.attr("data-src");
            String newUrl = MediaUtils.uploadImage(imgUrl);
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
