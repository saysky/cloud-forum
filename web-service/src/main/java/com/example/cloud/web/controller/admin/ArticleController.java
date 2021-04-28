package com.example.cloud.web.controller.admin;

import com.example.cloud.article.api.entity.Article;
import com.example.cloud.article.api.entity.Category;
import com.example.cloud.article.api.entity.Comment;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.api.feign.CategoryService;
import com.example.cloud.article.api.feign.CommentService;
import com.example.cloud.article.api.feign.TagService;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.entity.Answer;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.RelationshipService;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.web.controller.common.BaseController;
import com.example.cloud.common.enums.AllowCommentEnum;
import com.example.cloud.common.enums.PostStatusEnum;
import com.example.cloud.common.enums.PostStickyEnum;
import com.example.cloud.common.enums.SiteTitleEnum;
import com.example.cloud.web.util.BaiduPostUtil;
import com.example.cloud.web.util.HTMLUtil;
import com.example.cloud.web.util.IPUtil;
import com.example.cloud.common.vo.Response;
import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/4/13 下午5:08
 */

@Controller
public class ArticleController extends BaseController {


    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String hostName = "http://codergroup.cn";

    /**
     * 文章列表
     *
     * @param orderby
     * @param keywords
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/articles")
    public ModelAndView articleList(
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {


        //1、文章列表
        String orderBy = "";

        if (orderby.equals("hot")) {
            orderBy = "comment_size DESC, zan_size DESC, view_size DESC, id DESC";
        }
        //按访问量排序
        else if (orderby.equals("view")) {
            orderBy = "view_size DESC";
        }
        //按评论数排序
        else if (orderby.equals("comment")) {
            orderBy = "comment_size DESC";
        }
        //按点赞数排序
        else if (orderby.equals("like")) {
            orderBy = "zan_size DESC";
        }
        //最新查询
        else {
            orderBy = "id DESC";
        }
        Article condition = new Article();
        condition.setOrderBy(orderBy);
        condition.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
        condition.setTitle(keywords);
        PageVO<Article> articlePage = articleService.findAll(condition, pageIndex, pageSize);
        for (Article temp : articlePage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        model.addAttribute("page", articlePage);
        model.addAttribute("keywords", keywords);
        model.addAttribute("site_title", SiteTitleEnum.HOME_INDEX.getTitle());
        model.addAttribute("orderby", orderby);

        if (!async) {
            //热门文章
            List<Article> hotArticleList = articleService.listTop10HotArticles();
            model.addAttribute("hotArticleList", hotArticleList);
        }
        return new ModelAndView(async == true ? "home/article_list :: #tab-pane" : "home/article_list");
    }


    /**
     * 文章详情页
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/articles/{id}")
    public ModelAndView queryById(@PathVariable("id") Long id,
                                  HttpSession session,
                                  @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                  @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
                                  @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                  Model model) throws NotFoundException {
        User loginUser = (User) session.getAttribute("user");

        //1、判断文章是否存在
        Article article = articleService.queryById(id);
        if (article == null) {
            throw new NotFoundException("文章不存在!");
        }

        article.setUser(userService.queryById(article.getUserId()));
        article.setCategory(categoryService.queryById(article.getCategoryId()));

        // 2、判断操作用户是否是博客的所有者
        boolean isArticleOwner = false;
        if (loginUser != null) {
            isArticleOwner = Objects.equals(loginUser.getId(), article.getUserId());
        }

        //3、判断用户是否可以访问该文章
        //作者无法看到被删除的文章
        if (isArticleOwner) {
            if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), article.getStatus())) {
                throw new NotFoundException("文章已被删除!");
            }
        } else {
            //非作者无法看到除了已发布的文章
            if (!Objects.equals(PostStatusEnum.PUBLISH_POST.getCode(), article.getStatus())) {
                throw new NotFoundException("该文章可能被作者临时隐藏，暂时无法访问!");
            }
        }

//        //4、分类列表
        if (!async) {
            List<Category> categoryList = categoryService.findByUserId(article.getUserId());
            model.addAttribute("categoryList", categoryList);


            //5、判断是否关注了该用户
            Integer isFollow = 0;
            if (loginUser != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(loginUser.getId(), article.getUserId());
            }
            model.addAttribute("isFollow", isFollow);

            //7、站点关键词和描述
            model.addAttribute("site_keywords", article.getTags());
            model.addAttribute("site_content", article.getSummary());

        }

        //6、初始化评论
        String commentOrder = "";
        PageVO<Comment> commentPage = null;
        String orderBy = "";
        try {
            if (orderby.equals("hot")) { // 最热查询
                orderBy = "is_sticky DESC, zan_size DESC, id DESC";
                commentOrder = "hot";
            } else { // 最新查询
                orderBy = "is_sticky DESC, id DESC";
                commentOrder = "new";
            }
            Comment condition = new Comment();
            condition.setOrderBy(orderBy);
            condition.setArticleId(article.getId());
            condition.setPid(0L);
            condition.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            commentPage = commentService.findAll(condition, pageIndex, pageSize);
            for (Comment temp : commentPage.getContent()) {
                temp.setCommentList(commentService.findByPid(temp.getId()));
                temp.setUser(userService.queryById(temp.getUserId()));
                temp.setReplyUser(userService.queryById(temp.getReplyUserId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("isArticleOwner", isArticleOwner);
        model.addAttribute("article", article);
        model.addAttribute("page", commentPage);
        model.addAttribute("commentOrder", commentOrder);
        model.addAttribute("commentSize", article.getCommentSize());

        model.addAttribute("site_title", article.getTitle() + SiteTitleEnum.BLOG_DETAIL_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/article_detail :: #comment" : "home/article_detail");
    }


    //    用户中心
    @GetMapping("/manage/articles")
    public ModelAndView articleList(Model model,
                                    @RequestParam(value = "status", required = false, defaultValue = "all") String status,
                                    @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
                                    @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                    @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        String postStatus = "";
        //全部
        Article condition = new Article();
        condition.setUserId(user.getId());
        condition.setStatus(status);
        condition.setTitle(keywords);
        PageVO<Article> page = articleService.findAll(condition, pageIndex, pageSize);
        for (Article temp : page.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        model.addAttribute("page", page);
        model.addAttribute("site_title", SiteTitleEnum.BLOG_MANAGE.getTitle());
        model.addAttribute("status", postStatus);
        model.addAttribute("keywords", keywords);
        return new ModelAndView(async == true ? "home/userspace/article/list :: #tab-pane" : "home/userspace/article/list");
    }

    /**
     * 获取新增博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/post-new")
    public String createArticle(Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "home/login/login";
        }
        List<Category> categoryList = categoryService.findByUserId(user.getId());

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("site_title", SiteTitleEnum.BLOG_CREATE.getTitle());
        return "home/userspace/article/add";
    }


    /**
     * 获取编辑博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/manage/article/edit/{id}")
    public String editArticle(@PathVariable("id") Long id, Model model) throws Exception {
        Article article = null;
        try {
            article = articleService.queryById(id);
        } catch (Exception e) {
            throw new NotFoundException("文章不存在！");
        }
        article.setCategory(categoryService.queryById(article.getCategoryId()));
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "home/login/login";
        }

        if (!Objects.equals(user.getId(), article.getUserId())) {
            throw new Exception("没有权限操作！");
        }

        List<Category> categoryList = categoryService.findByUserId(user.getId());

        model.addAttribute("article", article);
        model.addAttribute("categoryList", categoryList);

        model.addAttribute("site_title", SiteTitleEnum.BLOG_EDIT.getTitle());
        return "home/userspace/article/edit";
    }


    @PostMapping("/manage/article/stick")

    public ResponseEntity<Response> stickArticle(Long id) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        //判断文章不存在
        Article article = articleService.queryById(id);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在！"));
        }

        //判断文章是否属于主人
        if (article.getUserId().equals(user.getId())) {
            article.setIsSticky(PostStickyEnum.STICKY_POST.getCode());
            articleService.update(article);
        } else {
            return ResponseEntity.ok().body(new Response(false, "没有权限！"));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功！"));
    }

    @PostMapping("/manage/article/cancelStick")
    public ResponseEntity<Response> cancelStickArticle(Long id) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        //判断文章不存在
        Article article = articleService.queryById(id);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在！"));
        }

        //判断文章是否属于主人
        if (article.getUserId().equals(user.getId())) {
            article.setIsSticky(PostStickyEnum.NOT_STICKY_POST.getCode());
            articleService.update(article);
        } else {
            return ResponseEntity.ok().body(new Response(false, "没有权限！"));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功！"));
    }

    /**
     * 发布文章提交
     *
     * @param article
     * @return
     */
    @PostMapping("/article/edit")

    public ResponseEntity<Response> saveArticle(@RequestBody Article article) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        Category category = null;
        //1、判断分类
        if (article.getCategoryId() == null) {
            return ResponseEntity.ok().body(new Response(false, "未选择分类"));
        } else {
            //处理分类
            category = categoryService.queryById(article.getCategoryId());
            if (category == null || !Objects.equals(category.getUserId(), user.getId())) {
                return ResponseEntity.ok().body(new Response(false, "分类不存在！"));
            }
        }


        String guid = null;


        try {
            //3、 判断是修改还是新增
            if (article.getId() != null) {
                //修改
                Article originalArticle = articleService.queryById(article.getId());
                originalArticle.setTitle(article.getTitle());
                originalArticle.setContent(article.getContent());
                originalArticle.setCategoryId(category.getId());
                originalArticle.setTags(article.getTags());
                originalArticle.setStatus(article.getStatus());
                originalArticle.setIsAllowComment(article.getIsAllowComment());
                originalArticle.setIsSticky(article.getIsSticky());
                int articleContentLength = article.getContent().length();
                //summary
                String summary = null;
                if (articleContentLength > 150) {
                    summary = HTMLUtil.html2text(article.getContent().substring(0, 150));
                } else {
                    summary = HTMLUtil.html2text(article.getContent().substring(0, articleContentLength));
                }
                originalArticle.setSummary(summary);
                articleService.update(originalArticle);
                guid = originalArticle.getGuid();
                //百度推送POST提交
                String url = hostName + guid;
                String[] arr = {url};
                BaiduPostUtil.Post(arr);

            } else {
                //用户
                article.setUserId(user.getId());
                int articleContentLength = article.getContent().length();
                //summary
                String summary = "";
                if (articleContentLength > 150) {
                    summary = HTMLUtil.html2text(article.getContent().substring(0, 150));
                } else {
                    summary = HTMLUtil.html2text(article.getContent().substring(0, articleContentLength));
                }
                article.setSummary(summary);
                article.setZanSize(0);
                article.setBookmarkSize(0);
                article.setCommentSize(0);
                article.setViewSize(0);
                article.setIsSticky(PostStickyEnum.NOT_STICKY_POST.getCode());
                article = articleService.insert(article);
                guid = "/articles/" + article.getId();
                article.setGuid(guid);
                articleService.update(article);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        logger.info("IP" + IPUtil.getIpAddr(request) + "发布了文章:" + article.getTitle());
        return ResponseEntity.ok().body(new Response(true, "处理成功", guid));
    }


    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    @DeleteMapping("/article/{id}")
    public ResponseEntity<Response> deleteArticle(@PathVariable("id") Long id) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        Article article = null;
        //1、判断文章是否存在
        try {
            article = articleService.queryById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在!"));

        }

        //2、判断文章是否属于该用户
        if (!user.getId().equals(article.getUserId())) {
            return ResponseEntity.ok().body(new Response(false, "没有权限!"));
        }

        //3、开始删除
        try {
            articleService.deleteById(article.getId());
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 开启评论
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/openComment")

    public ResponseEntity<Response> openComment(Long articleId) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        Article article = null;
        //1、判断文章是否存在
        try {
            article = articleService.queryById(articleId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在!"));

        }

        //2、判断文章是否属于该用户
        if (!user.getId().equals(article.getUserId())) {
            return ResponseEntity.ok().body(new Response(false, "没有权限!"));
        }

        //3、开始更新
        try {
            article.setIsAllowComment(AllowCommentEnum.ALLOW_COMMENT.getCode());
            articleService.update(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 关闭评论
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/closeComment")
    public ResponseEntity<Response> closeComment(Long articleId) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        //1、判断文章是否存在
        Article article = articleService.queryById(articleId);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在!"));
        }

        //2、判断文章是否属于该用户
        if (!user.getId().equals(article.getUserId())) {
            return ResponseEntity.ok().body(new Response(false, "没有权限!"));
        }

        //3、开始更新
        try {
            article.setIsAllowComment(AllowCommentEnum.NOT_ALLOW_COMMENT.getCode());
            articleService.update(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 增加访问量
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/increaseView")
    @ResponseBody
    public ResponseEntity<Response> increaseView(Long articleId) {
        Article article = null;
        try {
            article = articleService.viewIncrease(articleId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(true, "文章不存在！"));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功", article.getViewSize()));
    }

    //    后台
    @GetMapping("/admin/article")
    public ModelAndView adminArticleList(
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        //1、文章列表
        String orderBy;
        if (orderby.equals("hot")) {
            orderBy = "comment_size DESC, zan_size DESC, view_size DESC";
        }
        //按访问量排序
        else if (orderby.equals("view")) {
            orderBy = "view_size DESC";
        }
        //按评论数排序
        else if (orderby.equals("comment")) {
            orderBy = "comment_size DESC";
        }
        //按点赞数排序
        else if (orderby.equals("like")) {
            orderBy = "zan_size DESC";
        } else if (orderby.equals("bookmark")) {
            orderBy = "bookmark_size DESC";
        }
        //最新查询
        else {
            orderBy = "id DESC";
        }

        Article condition = new Article();
        condition.setTitle(keywords);
        condition.setStatus(status);
        condition.setOrderBy(orderBy);
        PageVO<Article> articlePage = articleService.findAll(condition, pageIndex, pageSize);
        for (Article temp : articlePage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        if (!async) {
            Long publishSize = articleService.countArticleByStatus(PostStatusEnum.PUBLISH_POST.getCode());
            Long draftSize = articleService.countArticleByStatus(PostStatusEnum.DRAFT_POST.getCode());
            Long privateSize = articleService.countArticleByStatus(PostStatusEnum.PRIVATE_POST.getCode());
            Long deleteSize = articleService.countArticleByStatus(PostStatusEnum.DELETED_POST.getCode());
            Long allSize = publishSize + draftSize + privateSize + deleteSize;

            model.addAttribute("allSize", allSize);
            model.addAttribute("publishSize", publishSize);
            model.addAttribute("draftSize", draftSize);
            model.addAttribute("privateSize", privateSize);
            model.addAttribute("deleteSize", deleteSize);
        }

        model.addAttribute("page", articlePage);
        model.addAttribute("keywords", keywords);
        model.addAttribute("site_title", SiteTitleEnum.HOME_INDEX.getTitle());
        model.addAttribute("orderby", orderby);
        model.addAttribute("status", status);
        return new ModelAndView(async == true ? "admin/article/list :: #mainContainerReplace" : "admin/article/list");
    }


    @DeleteMapping("/admin/article/{id}")
    public ResponseEntity<Response> deleteCommentByAdmin(@PathVariable("id") Long id) {
        try {
            articleService.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 还原文章
     *
     * @param id
     * @return
     */
    @PutMapping("/admin/article/restore/{id}")

    public ResponseEntity<Response> restoreComment(@PathVariable("id") Long id) {
        try {
            Article article = articleService.queryById(id);
            article.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            articleService.update(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 批量删除
     *
     * @param ids 库Id列表
     * @return JsonResult
     */
    @PostMapping(value = "/admin/article/batchRemove")
    @ResponseBody
    public ResponseEntity<Response> batchRemove(@RequestParam("ids") String ids) {
        String[] arr = ids.split(",");
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(Long.valueOf(arr[i]));
        }
        try {
            articleService.batchRemove(list);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 清空库
     *
     * @return JsonResult
     */
    @PostMapping(value = "/admin/article/clear")
    @ResponseBody
    public ResponseEntity<Response> clearAll() {
        try {
            articleService.deleteByStatus(PostStatusEnum.DELETED_POST.getCode());
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "清空成功！"));
    }

}
