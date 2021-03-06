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
 * @author θ¨ζ
 * @date 2018/4/13 δΈε5:08
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
     * ζη« εθ‘¨
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


        //1γζη« εθ‘¨
        String orderBy = "";

        if (orderby.equals("hot")) {
            orderBy = "comment_size DESC, zan_size DESC, view_size DESC, id DESC";
        }
        //ζθ?Ώι?ιζεΊ
        else if (orderby.equals("view")) {
            orderBy = "view_size DESC";
        }
        //ζθ―θ?Ίζ°ζεΊ
        else if (orderby.equals("comment")) {
            orderBy = "comment_size DESC";
        }
        //ζηΉθ΅ζ°ζεΊ
        else if (orderby.equals("like")) {
            orderBy = "zan_size DESC";
        }
        //ζζ°ζ₯θ―’
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
            //η­ι¨ζη« 
            List<Article> hotArticleList = articleService.listTop10HotArticles();
            model.addAttribute("hotArticleList", hotArticleList);
        }
        return new ModelAndView(async == true ? "home/article_list :: #tab-pane" : "home/article_list");
    }


    /**
     * ζη« θ―¦ζι‘΅
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

        //1γε€ζ­ζη« ζ―ε¦ε­ε¨
        Article article = articleService.queryById(id);
        if (article == null) {
            throw new NotFoundException("ζη« δΈε­ε¨!");
        }

        article.setUser(userService.queryById(article.getUserId()));
        article.setCategory(categoryService.queryById(article.getCategoryId()));

        // 2γε€ζ­ζδ½η¨ζ·ζ―ε¦ζ―εε?’ηζζθ
        boolean isArticleOwner = false;
        if (loginUser != null) {
            isArticleOwner = Objects.equals(loginUser.getId(), article.getUserId());
        }

        //3γε€ζ­η¨ζ·ζ―ε¦ε―δ»₯θ?Ώι?θ―₯ζη« 
        //δ½θζ ζ³ηε°θ’«ε ι€ηζη« 
        if (isArticleOwner) {
            if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), article.getStatus())) {
                throw new NotFoundException("ζη« ε·²θ’«ε ι€!");
            }
        } else {
            //ιδ½θζ ζ³ηε°ι€δΊε·²εεΈηζη« 
            if (!Objects.equals(PostStatusEnum.PUBLISH_POST.getCode(), article.getStatus())) {
                throw new NotFoundException("θ―₯ζη« ε―θ½θ’«δ½θδΈ΄ζΆιθοΌζζΆζ ζ³θ?Ώι?!");
            }
        }

//        //4γεη±»εθ‘¨
        if (!async) {
            List<Category> categoryList = categoryService.findByUserId(article.getUserId());
            model.addAttribute("categoryList", categoryList);


            //5γε€ζ­ζ―ε¦ε³ζ³¨δΊθ―₯η¨ζ·
            Integer isFollow = 0;
            if (loginUser != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(loginUser.getId(), article.getUserId());
            }
            model.addAttribute("isFollow", isFollow);

            //7γη«ηΉε³ι?θ―εζθΏ°
            model.addAttribute("site_keywords", article.getTags());
            model.addAttribute("site_content", article.getSummary());

        }

        //6γεε§εθ―θ?Ί
        String commentOrder = "";
        PageVO<Comment> commentPage = null;
        String orderBy = "";
        try {
            if (orderby.equals("hot")) { // ζη­ζ₯θ―’
                orderBy = "is_sticky DESC, zan_size DESC, id DESC";
                commentOrder = "hot";
            } else { // ζζ°ζ₯θ―’
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


    //    η¨ζ·δΈ­εΏ
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
        //ε¨ι¨
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
     * θ·εζ°ε’εε?’ηηι’
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
     * θ·εηΌθΎεε?’ηηι’
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
            throw new NotFoundException("ζη« δΈε­ε¨οΌ");
        }
        article.setCategory(categoryService.queryById(article.getCategoryId()));
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "home/login/login";
        }

        if (!Objects.equals(user.getId(), article.getUserId())) {
            throw new Exception("ζ²‘ζζιζδ½οΌ");
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
            return ResponseEntity.ok().body(new Response(false, "η¨ζ·ζͺη»ε½!"));
        }

        //ε€ζ­ζη« δΈε­ε¨
        Article article = articleService.queryById(id);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "ζη« δΈε­ε¨οΌ"));
        }

        //ε€ζ­ζη« ζ―ε¦ε±δΊδΈ»δΊΊ
        if (article.getUserId().equals(user.getId())) {
            article.setIsSticky(PostStickyEnum.STICKY_POST.getCode());
            articleService.update(article);
        } else {
            return ResponseEntity.ok().body(new Response(false, "ζ²‘ζζιοΌ"));
        }
        return ResponseEntity.ok().body(new Response(true, "ζδ½ζεοΌ"));
    }

    @PostMapping("/manage/article/cancelStick")
    public ResponseEntity<Response> cancelStickArticle(Long id) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "η¨ζ·ζͺη»ε½!"));
        }

        //ε€ζ­ζη« δΈε­ε¨
        Article article = articleService.queryById(id);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "ζη« δΈε­ε¨οΌ"));
        }

        //ε€ζ­ζη« ζ―ε¦ε±δΊδΈ»δΊΊ
        if (article.getUserId().equals(user.getId())) {
            article.setIsSticky(PostStickyEnum.NOT_STICKY_POST.getCode());
            articleService.update(article);
        } else {
            return ResponseEntity.ok().body(new Response(false, "ζ²‘ζζιοΌ"));
        }
        return ResponseEntity.ok().body(new Response(true, "ζδ½ζεοΌ"));
    }

    /**
     * εεΈζη« ζδΊ€
     *
     * @param article
     * @return
     */
    @PostMapping("/article/edit")

    public ResponseEntity<Response> saveArticle(@RequestBody Article article) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "η¨ζ·ζͺη»ε½!"));
        }
        Category category = null;
        //1γε€ζ­εη±»
        if (article.getCategoryId() == null) {
            return ResponseEntity.ok().body(new Response(false, "ζͺιζ©εη±»"));
        } else {
            //ε€ηεη±»
            category = categoryService.queryById(article.getCategoryId());
            if (category == null || !Objects.equals(category.getUserId(), user.getId())) {
                return ResponseEntity.ok().body(new Response(false, "εη±»δΈε­ε¨οΌ"));
            }
        }


        String guid = null;


        try {
            //3γ ε€ζ­ζ―δΏ?ζΉθΏζ―ζ°ε’
            if (article.getId() != null) {
                //δΏ?ζΉ
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
                //ηΎεΊ¦ζ¨ιPOSTζδΊ€
                String url = hostName + guid;
                String[] arr = {url};
                BaiduPostUtil.Post(arr);

            } else {
                //η¨ζ·
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

        logger.info("IP" + IPUtil.getIpAddr(request) + "εεΈδΊζη« :" + article.getTitle());
        return ResponseEntity.ok().body(new Response(true, "ε€ηζε", guid));
    }


    /**
     * ε ι€ζη« 
     *
     * @param id
     * @return
     */
    @DeleteMapping("/article/{id}")
    public ResponseEntity<Response> deleteArticle(@PathVariable("id") Long id) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "η¨ζ·ζͺη»ε½!"));
        }

        Article article = null;
        //1γε€ζ­ζη« ζ―ε¦ε­ε¨
        try {
            article = articleService.queryById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "ζη« δΈε­ε¨!"));

        }

        //2γε€ζ­ζη« ζ―ε¦ε±δΊθ―₯η¨ζ·
        if (!user.getId().equals(article.getUserId())) {
            return ResponseEntity.ok().body(new Response(false, "ζ²‘ζζι!"));
        }

        //3γεΌε§ε ι€
        try {
            articleService.deleteById(article.getId());
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "ε€ηζε"));
    }

    /**
     * εΌε―θ―θ?Ί
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/openComment")

    public ResponseEntity<Response> openComment(Long articleId) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "η¨ζ·ζͺη»ε½!"));
        }
        Article article = null;
        //1γε€ζ­ζη« ζ―ε¦ε­ε¨
        try {
            article = articleService.queryById(articleId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "ζη« δΈε­ε¨!"));

        }

        //2γε€ζ­ζη« ζ―ε¦ε±δΊθ―₯η¨ζ·
        if (!user.getId().equals(article.getUserId())) {
            return ResponseEntity.ok().body(new Response(false, "ζ²‘ζζι!"));
        }

        //3γεΌε§ζ΄ζ°
        try {
            article.setIsAllowComment(AllowCommentEnum.ALLOW_COMMENT.getCode());
            articleService.update(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "ε€ηζε"));
    }

    /**
     * ε³ι­θ―θ?Ί
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/closeComment")
    public ResponseEntity<Response> closeComment(Long articleId) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "η¨ζ·ζͺη»ε½!"));
        }

        //1γε€ζ­ζη« ζ―ε¦ε­ε¨
        Article article = articleService.queryById(articleId);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "ζη« δΈε­ε¨!"));
        }

        //2γε€ζ­ζη« ζ―ε¦ε±δΊθ―₯η¨ζ·
        if (!user.getId().equals(article.getUserId())) {
            return ResponseEntity.ok().body(new Response(false, "ζ²‘ζζι!"));
        }

        //3γεΌε§ζ΄ζ°
        try {
            article.setIsAllowComment(AllowCommentEnum.NOT_ALLOW_COMMENT.getCode());
            articleService.update(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "ε€ηζε"));
    }

    /**
     * ε’ε θ?Ώι?ι
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
            return ResponseEntity.ok().body(new Response(true, "ζη« δΈε­ε¨οΌ"));
        }
        return ResponseEntity.ok().body(new Response(true, "ε€ηζε", article.getViewSize()));
    }

    //    εε°
    @GetMapping("/admin/article")
    public ModelAndView adminArticleList(
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        //1γζη« εθ‘¨
        String orderBy;
        if (orderby.equals("hot")) {
            orderBy = "comment_size DESC, zan_size DESC, view_size DESC";
        }
        //ζθ?Ώι?ιζεΊ
        else if (orderby.equals("view")) {
            orderBy = "view_size DESC";
        }
        //ζθ―θ?Ίζ°ζεΊ
        else if (orderby.equals("comment")) {
            orderBy = "comment_size DESC";
        }
        //ζηΉθ΅ζ°ζεΊ
        else if (orderby.equals("like")) {
            orderBy = "zan_size DESC";
        } else if (orderby.equals("bookmark")) {
            orderBy = "bookmark_size DESC";
        }
        //ζζ°ζ₯θ―’
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
        return ResponseEntity.ok().body(new Response(true, "ε€ηζε"));
    }


    /**
     * θΏεζη« 
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
        return ResponseEntity.ok().body(new Response(true, "ε€ηζε"));
    }


    /**
     * ζΉιε ι€
     *
     * @param ids εΊIdεθ‘¨
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
        return ResponseEntity.ok().body(new Response(true, "ε€ηζε"));
    }

    /**
     * ζΈη©ΊεΊ
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
        return ResponseEntity.ok().body(new Response(true, "ζΈη©ΊζεοΌ"));
    }

}
