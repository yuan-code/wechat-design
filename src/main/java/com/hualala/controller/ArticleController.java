package com.hualala.controller;


import com.hualala.common.ResultCode;
import com.hualala.common.UserResolver;
import com.hualala.exception.BusinessException;
import com.hualala.model.Article;
import com.hualala.model.Order;
import com.hualala.model.User;
import com.hualala.service.ArticleService;
import com.hualala.service.OrderService;
import com.hualala.service.UserService;
import com.hualala.util.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Optional;

/**
 * <p>
 * 文章表 前端控制器
 * </p>
 *
 * @author YuanChong
 * @since 2019-07-08
 */
@Controller
@RequestMapping("/article")
public class ArticleController {


    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * 查看文章详情 需要微信基础授权
     *
     * @param articleid
     * @param modelMap
     * @param user 访问页面的用户
     * @throws IOException
     */
    @RequestMapping("/auth/detail/{articleid}")
    public String articleDetail(@PathVariable("articleid") Long articleid, ModelMap modelMap, @UserResolver User user) {
        Article article = articleService.getById(articleid);
        if (article.getPid() != 0L) {
            //二次编辑文章查询所属用户
            User author = userService.getById(article.getUserid());
            modelMap.addAttribute("author", author);

            //TODO 增加阅读用户
        }
        modelMap.addAttribute("article", article);
        return "article/article";
    }


    /**
     * 编辑文章 需要微信基础授权
     *
     * @param articleid
     * @param modelMap
     * @param user 访问页面的用户
     * @throws IOException
     */
    @RequestMapping("/passport/edit/{articleid}")
    public String articleEdit(@PathVariable("articleid") Long articleid, ModelMap modelMap, @UserResolver User user) {
        Article article = articleService.getById(articleid);
        modelMap.addAttribute("article", article);
        return "article/edit";
    }



    /**
     * 复制文章
     *
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/passport/copy")
    public Object articleCopy(Article article, @UserResolver User user) throws IOException {
        Article copy = articleService.articleCopy(article.getSource());
        return ResultUtils.success(copy);
    }

    /**
     * 保存文章 需要微信基础授权
     *
     * @param article
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/passport/save")
    public Object articleSave(Article article, @UserResolver User user) {
        if(StringUtils.isEmpty(article.getContent())) {
            throw new BusinessException(ResultCode.PARAMS_LOST.getCode(),"文章内容必传");
        }
        if(StringUtils.isEmpty(article.getTitle())) {
            throw new BusinessException(ResultCode.PARAMS_LOST.getCode(),"文章标题必传");
        }
        if(article.getPid() == null || article.getPid() == 0L) {
            throw new BusinessException(ResultCode.PARAMS_LOST.getCode(),"父文章ID必传");
        }
        if(!user.isAvailable()) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(),"用户未付费");
        }
        Article copy = articleService.getById(article.getPid());
        copy.setContent(article.getContent());
        copy.setThumbnail(article.getTitle());
        copy.setArticleid(null);
        copy.setPid(article.getPid());
        copy.setUserid(user.getUserid());
        copy.setOpenid(user.getOpenid());
        articleService.save(copy);
        return ResultUtils.success(copy);
    }


}
