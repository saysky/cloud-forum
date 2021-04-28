package com.example.cloud.web.controller.front;

import com.example.cloud.article.api.entity.Article;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.api.feign.CategoryService;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.entity.Answer;
import com.example.cloud.qa.api.entity.Question;
import com.example.cloud.qa.api.feign.AnswerService;
import com.example.cloud.qa.api.feign.QuestionService;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.RelationshipService;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.web.controller.common.BaseController;
import com.example.cloud.common.enums.PostStatusEnum;
import com.example.cloud.common.enums.SiteTitleEnum;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 个人主页
 *
 * @author 言曌
 * @date 2018/4/24 下午1:15
 */


@Controller
public class HomePageController extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @GetMapping(value = "/main")
    public String homePage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        } else {
            User user = (User) session.getAttribute("user");
            String username = user.getUsername();
            return "redirect:/" + username + "/articles";
        }
    }


    /**
     * 用户主页
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/articles")
    public ModelAndView articleList(@PathVariable("username") String username,
                                    @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
                                    @RequestParam(value = "category", required = false) Long categoryId,
                                    @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
                                    @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                    @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize,
                                    HttpSession session, Model model) throws NotFoundException {


        User loginUser = (User) session.getAttribute("user");

        //1、判断用户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }

        //2、文章列表
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
        condition.setUserId(user.getId());
        condition.setTitle(keywords);
        condition.setCategoryId(categoryId);
        condition.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
        condition.setOrderBy(orderBy);
        PageVO<Article> articlePage = articleService.findAll(condition, pageIndex, pageSize);
        for (Article temp : articlePage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }

        if (!async) {
            //2、判断当前用户是否为该博客主人
            Boolean isOwner = false;
            if (loginUser != null) {
                isOwner = Objects.equals(user.getId(), loginUser.getId());
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (loginUser != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(loginUser.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }

        model.addAttribute("page", articlePage);
        model.addAttribute("keywords", keywords);
        model.addAttribute("category", categoryId);
        model.addAttribute("user", user);
        model.addAttribute("orderby", "createTime");
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/article :: #tab-pane" : "home/homepage/article");
    }


    /**
     * 该用户提出的问题
     *
     * @param username
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/questions")
    public ModelAndView questionList(@PathVariable("username") String username,
                                     @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                     @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize, Model model) throws NotFoundException {
        User loginUser = (User) session.getAttribute("user");

        //1、判断用户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }

        if (!async) {
            //2、判断当前用户是否为该博客主人
            Boolean isOwner = false;
            if (loginUser != null) {
                isOwner = Objects.equals(user.getId(), loginUser.getId());
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (loginUser != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(loginUser.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }
        //4、回答列表
        Question condition = new Question();
        condition.setUserId(user.getId());
        PageVO<Question> questionPage = questionService.findAll(condition, pageIndex, pageSize);
        for (Question temp : questionPage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }

        model.addAttribute("page", questionPage);
        model.addAttribute("user", user);
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/question :: #tab-pane" : "home/homepage/question");
    }

    /**
     * 该用户提出的问题
     *
     * @param username
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/answers")
    public ModelAndView answerList(@PathVariable("username") String username,
                                   @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize, Model model) throws NotFoundException {
        User loginUser = (User) session.getAttribute("user");

        //1、判断用户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }

        if (!async) {
            //2、判断当前用户是否为该博客主人
            Boolean isOwner = false;
            if (loginUser != null) {
                isOwner = Objects.equals(user.getId(), loginUser.getId());
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (loginUser != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(loginUser.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }
        //4、回答列表
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        Answer condition = new Answer();
        condition.setUserId(user.getId());
        PageVO<Answer> answerPage = answerService.findAll(condition, pageIndex, pageSize);
        for (Answer answer : answerPage.getContent()) {
            answer.setUser(userService.queryById(answer.getUserId()));
            answer.setQuestion(questionService.queryById(answer.getQuestionId()));
        }
        model.addAttribute("page", answerPage);
        model.addAttribute("user", user);
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/answer :: #tab-pane" : "home/homepage/answer");
    }


    //关系页面
    @GetMapping(value = "/{username}/follows")
    public ModelAndView followList(@PathVariable("username") String username,
                                   @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) throws NotFoundException {
        User loginUser = (User) session.getAttribute("user");

        //1、判断用户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }

        if (!async) {
            //2、判断当前用户是否为该博客主人
            Boolean isOwner = false;
            if (loginUser != null) {
                isOwner = Objects.equals(user.getId(), loginUser.getId());
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (loginUser != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(loginUser.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }
        //4、分页显示
        PageVO<User> userPage = relationshipService.listFollows(user.getId(), pageIndex, pageSize);
        model.addAttribute("page", userPage);
        model.addAttribute("user", user);
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/follow :: #tab-pane" : "home/homepage/follow");
    }

    //关系页面
    @GetMapping(value = "/{username}/fans")
    public ModelAndView fanList(@PathVariable("username") String username,
                                @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) throws NotFoundException {
        User loginUser = (User) session.getAttribute("user");

        //1、判断用户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }

        if (!async) {
            //2、判断当前用户是否为该博客主人
            Boolean isOwner = false;
            if (loginUser != null) {
                isOwner = Objects.equals(user.getId(), loginUser.getId());
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (loginUser != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(loginUser.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }
        //4、分页显示
        PageVO<User> userPage = relationshipService.listFans(user.getId(), pageIndex, pageSize);
        model.addAttribute("page", userPage);
        model.addAttribute("user", user);
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/fan :: #tab-pane" : "home/homepage/fan");
    }


}
