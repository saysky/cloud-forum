package com.example.cloud.web.controller.front;

import com.example.cloud.article.api.entity.Article;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.entity.Question;
import com.example.cloud.qa.api.feign.QuestionService;
import com.example.cloud.common.enums.PostStatusEnum;
import com.example.cloud.user.api.feign.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/23 上午11:14
 */

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/articles")
    public ModelAndView articleList(
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        String orderBy;
        //按访问量排序
        if (orderby.equals("viewSize")) {
            orderBy = "is_sticky DESC, view_size DESC";
        }
        //按评论数排序
        else if (orderby.equals("commentSize")) {
            orderBy = "is_sticky DESC, comment_size DESC";
        }
        //按点赞数排序
        else if (orderby.equals("zanSize")) {
            orderBy = "is_sticky DESC, zan_size DESC";
        }
        //最新查询
        else {
            orderBy = "is_sticky DESC, id DESC";
        }
        Article condition = new Article();
        condition.setTitle(keywords);
        condition.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
        condition.setOrderBy(orderBy);
        PageVO<Article> articlePage = articleService.findAll(condition, pageIndex, pageSize);
        for (Article temp : articlePage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        model.addAttribute("page", articlePage);
        model.addAttribute("url", "/search/articles");
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("keywords", keywords);
        return new ModelAndView(async == true ? "home/search/article :: #tab-pane" : "home/search/article");
    }


    @GetMapping("/questions")
    public ModelAndView questionList(
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize, Model model) {

        String orderBy = null;
        if ("new".equals(orderby)) {
            orderBy = "id DESC";
        } else if ("answer".equals(orderby)) {
            orderBy = "answer_size DESC";
        } else if ("view".equals(orderby)) {
            orderBy = "view_size DESC";
        } else {
            orderBy = "id DESC";
        }

        Question condition = new Question();
        condition.setOrderBy(orderBy);
        condition.setTitle(keywords);
        PageVO<Question> questionPage = questionService.findAll(condition, pageIndex, pageSize);
        for (Question temp : questionPage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        model.addAttribute("page", questionPage);
        model.addAttribute("url", "/search/questions");
        model.addAttribute("orderby", orderby);
        model.addAttribute("keywords", keywords);
        return new ModelAndView(async == true ? "home/search/question :: #tab-pane" : "home/search/question");
    }
}
