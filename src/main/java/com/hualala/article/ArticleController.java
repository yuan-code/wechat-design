package com.hualala.article;


import com.hualala.article.domain.Article;
import com.hualala.common.ResultCode;
import com.hualala.config.UserResolver;
import com.hualala.common.BusinessException;
import com.hualala.customer.domain.Customer;
import com.hualala.user.domain.User;
import com.hualala.customer.CustomerService;
import com.hualala.user.UserService;
import com.hualala.util.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Objects;

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
    private CustomerService customerService;

    /**
     * 查看文章详情 需要微信基础授权
     *
     * @param articleid
     * @param modelMap
     * @param user 访问页面的用户
     * @throws IOException
     */
    @RequestMapping("/detail/{articleid}")
    public String articleDetail(@PathVariable("articleid") Long articleid, ModelMap modelMap, @UserResolver User user) {
        Article article = articleService.getById(articleid);
        if (article.getPid() != 0L) {
            //二次编辑文章查询所属用户
            User author = userService.getById(article.getUserid());
            modelMap.addAttribute("author", author);
            if(!Objects.equals(author.getOpenid(),user.getOpenid())) {
                Customer customer = new Customer();
                customer.setArticleid(article.getArticleid());
                customer.setAuthorOpenid(author.getOpenid());
                customer.setAuthorUserid(author.getUserid());
                customer.setCustomerOpenid(user.getOpenid());
                customer.setCustomerUserid(user.getUserid());
                customerService.save(customer);
            }
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
    @RequestMapping("/edit/{articleid}")
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
    @RequestMapping("/copyArticle")
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
    @RequestMapping("/save")
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
