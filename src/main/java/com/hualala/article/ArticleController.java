package com.hualala.article;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hualala.article.domain.Article;
import com.hualala.common.ResultCode;
import com.hualala.util.LockHelper;
import com.hualala.util.TimeUtil;
import com.hualala.user.component.UserResolver;
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
import java.net.URLEncoder;
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

    @Autowired
    private LockHelper lockHelper;

    /**
     * 查看文章详情 需要微信基础授权
     *
     * @param articleid
     * @param modelMap
     * @param user      访问页面的用户
     * @throws IOException
     */
    @RequestMapping("/detail/{articleid}")
    public String articleDetail(@PathVariable("articleid") Long articleid, ModelMap modelMap, @UserResolver User user) throws Exception {
        Article article = articleService.getById(articleid);
        //1- 代表路人甲
        Integer userStatus = 0;
        Integer customerCount = 0;
        //所属用户ID
        if (article.getUserid() != 0L) {
            //二次编辑文章查询所属用户
            User author = userService.getById(article.getUserid());
            modelMap.addAttribute("author", author);
            if (!Objects.equals(author.getOpenid(), user.getOpenid())) {
                //对于其他人点击来的情况 增加关注量
                String lockKey = "addCustomer/" + URLEncoder.encode(author.getOpenid() + "/" + user.getOpenid(), "UTF-8");
                lockHelper.doSync(lockKey,() -> customerService.addCustomer(author,user,article.getArticleid()));
                userStatus = 1;
            }
            //查询作者的文章关注量
            customerCount = customerService.queryCustomerCount(article.getOpenid(), articleid);
        }
        modelMap.addAttribute("customerCount", customerCount);
        modelMap.addAttribute("article", article);
        modelMap.addAttribute("userStatus",userStatus);
        return "article/article";
    }


    /**
     * 编辑文章 需要微信基础授权
     *
     * @param articleid
     * @param modelMap
     * @param user      访问页面的用户
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
    public Object articleCopy(Article article, @UserResolver User user) throws Exception {
        String lockKey = "copyArticle/" + URLEncoder.encode(article.getSource(), "UTF-8");
        Article copy = lockHelper.doSync(lockKey,() -> articleService.articleCopy(article.getSource()));
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
        if (StringUtils.isEmpty(article.getContent())) {
            throw new BusinessException(ResultCode.PARAMS_LOST.getCode(), "文章内容必传");
        }
        if (StringUtils.isEmpty(article.getTitle())) {
            throw new BusinessException(ResultCode.PARAMS_LOST.getCode(), "文章标题必传");
        }
        if (article.getPid() == null || article.getPid() == 0L) {
            throw new BusinessException(ResultCode.PARAMS_LOST.getCode(), "父文章ID必传");
        }
        if (!user.isAvailable()) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "用户未付费");
        }
        if (StringUtils.isNotEmpty(article.getOpenid()) && !Objects.equals(article.getOpenid(), user.getOpenid())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "这篇文章不属于您");
        }
        Article source = articleService.getById(article.getPid());
        if (source.getPid() != null && source.getPid() != 0L) {
            //这种情况是对自己做编辑
            article.setPid(source.getPid());
            article.setArticleid(source.getArticleid());
        } else {
            article.setPid(source.getArticleid());
            article.setCreateTime(TimeUtil.currentDT());
        }
        article.setSummary(source.getSummary());
        article.setThumbnail(source.getThumbnail());
        article.setSource(source.getSource());
        article.setUserid(user.getUserid());
        article.setOpenid(user.getOpenid());
        article.setModifyTime(TimeUtil.currentDT());
        articleService.saveOrUpdate(article);
        return ResultUtils.success(article);
    }

    /**
     * 跳转文章列表页面 有可能是被人分享过
     *
     * @param openid
     * @param modelMap
     * @param user
     * @return
     */
    @RequestMapping("/custom/{openid}")
    public Object custom(@PathVariable("openid") String openid, ModelMap modelMap, @UserResolver User user) {
        User author = null;
        if (Objects.equals("-1",openid)) {
            author = user;
        } else {
            author = userService.queryByOpenid(openid);
        }
        modelMap.put("author", author);
        return "article/custom";
    }

    /**
     * 文章列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/list/{openid}")
    public Object list(@PathVariable("openid") String openid, Long pageNo, Long pageSize) {
        Page<Article> page = new Page<>(pageNo, pageSize);
        page.addOrder(OrderItem.asc("modify_Time"));
        QueryWrapper<Article> wrapper = new QueryWrapper<Article>()
                .select(Article.class, info -> !info.getColumn().equals("content") && !info.getColumn().equals("head"))
                .eq("openid", openid);
        IPage<Article> result = articleService.page(page, wrapper);
        return ResultUtils.success(result);
    }


}
