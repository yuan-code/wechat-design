package com.hualala.article;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualala.article.domain.Article;
import com.hualala.cos.MediaUtils;
import com.hualala.user.UserService;
import com.hualala.user.domain.User;
import com.hualala.util.CurrentUser;
import com.hualala.util.HttpClientUtil;
import com.hualala.util.TimeUtil;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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

    @Autowired
    private UserService userService;

    @Autowired
    private PlatformTransactionManager transactionManager;
    /**
     * 爬取单个公众号文章
     *
     * @param source
     * @return
     * @throws IOException
     */
    //@Transactional(rollbackFor = Exception.class)
    public Article articleCopy(String source,String openid) {
        User user = userService.queryByOpenid(openid);
        QueryWrapper<Article> wrapper = new QueryWrapper<Article>().eq("source", source).eq("pid", 0);
        //这行代码块是同步的
        Article article = articleMapper.selectOne(wrapper);
        if (article != null) {
            return article;
        }
        Document document = connetUrl(source);
        //处理图片防盗链
        Element jsContent = document.getElementById("js_content");

        String content = replaceImage(jsContent).toString();
//        content = content.replaceAll("<section", "<p");
//        content = content.replaceAll("</section>", "</p>");
        String title = document.select("#activity-name").text();
        //获取JS变量
        Map<String, String> variableMap = scriptVariable(document);
        String summary = variableMap.get("msg_desc");
        String thumbnail = variableMap.get("msg_cdn_url");
        byte[] bytes = HttpClientUtil.downLoadFromUrl(thumbnail);
        thumbnail = MediaUtils.uploadImage(bytes);
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            article = new Article();
            article.setContent(content);
            article.setTitle(title);
            article.setSummary(summary);
            article.setThumbnail(thumbnail);
            article.setSource(source);
            article.setCreateTime(TimeUtil.currentDT());
            article.setSourceOpenid(user.getOpenid());
            article.setSourceUserid(user.getUserid());
            articleMapper.insert(article);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            log.error("articleCopy transaction  error ,reason {}",e);
        }
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
            url.ifPresent(u -> ele.attr("style",style.replace(u, MediaUtils.uploadImage(u))));
        }
        Elements images = element.select("img");
        for (Element ele : images) {
            String imgUrl = ele.attr("data-src");
            String newUrl = MediaUtils.uploadImage(imgUrl);
            ele.attr("src", newUrl);
        }
        Elements hrefA = element.select("a");
        for (Element eleA : hrefA) {
            eleA.attr("href","javascript:void(0)");
        }

        Elements elements = element.getElementsByTag("p");
        for(int i = elements.size() - 1; i >= 0; i--) {
            Element eleP = elements.get(i);
            if(eleP.hasText()) {
                eleP.remove();
                break;
            }
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
