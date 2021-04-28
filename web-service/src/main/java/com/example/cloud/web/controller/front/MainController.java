package com.example.cloud.web.controller.front;

import com.alibaba.fastjson.JSON;
import com.example.cloud.article.api.entity.Article;
import com.example.cloud.article.api.entity.Tag;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.api.feign.TagService;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.feign.QuestionService;
import com.example.cloud.user.api.entity.Bulletin;
import com.example.cloud.user.api.entity.Slide;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.BulletinService;
import com.example.cloud.user.api.feign.SlideService;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.web.controller.common.BaseController;
import com.example.cloud.common.enums.PostStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 所有用户都可以访问
 *
 * @author 言曌
 * @date 2018/3/19 下午3:14
 */

@Controller
public class MainController extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private SlideService slideService;


    @Autowired
    private ArticleService articleService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private BulletinService bulletinService;

    @Autowired
    private TagService tagService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping("/")
    public ModelAndView index(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                              @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize, Model model) {

        //文章
        Article condition = new Article();
        condition.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
        condition.setOrderBy("id DESC");
        PageVO<Article> articlePage = articleService.findAll(condition, pageIndex, pageSize);
        for (Article article : articlePage.getContent()) {
            article.setUser(userService.queryById(article.getUserId()));
        }
        model.addAttribute("page", articlePage);

        if (!async) {
            //幻灯片
            List<Slide> slideList = slideService.queryAllByLimit(0, 100);
            model.addAttribute("slideList", slideList);

            //公告
            List<Bulletin> bulletinList = bulletinService.queryAllByLimit(0, 100);
            model.addAttribute("bulletinList", bulletinList);
        }
        return new ModelAndView(async == true ? "index :: #article-box-replace" : "index");
    }

    @PostMapping("/ajax/checkUsernameOrEmail")
    @ResponseBody
    public String checkUsernameOrEmail(Long id, String username, String email) {
        HashMap<String, Boolean> hashMap = new HashMap();
        User user = null;
        if (username != null && email == null) {
            user = userService.findByUsername(username);
        } else {
            user = userService.findByEmail(email);
        }
        //新增用户
        if (id == null) {
            if (user == null) {
                hashMap.put("valid", true);
            } else {
                hashMap.put("valid", false);
            }
        }
        //编辑用户
        else {
            //编辑的是当前的用户
            if (user == null || Objects.equals(user.getId(), id)) {
                hashMap.put("valid", true);
            } else {
                hashMap.put("valid", false);
            }
        }
        //可用

        return JSON.toJSONString(hashMap);
    }

}
