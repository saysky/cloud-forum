package com.example.cloud.web.controller.admin;

import com.example.cloud.article.api.entity.Article;
import com.example.cloud.article.api.entity.Comment;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.api.feign.CommentService;
import com.example.cloud.common.enums.CommentStickStatusEnum;
import com.example.cloud.common.enums.PostStatusEnum;
import com.example.cloud.common.enums.SiteTitleEnum;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.entity.Answer;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.common.vo.Response;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/4/18 下午3:29
 */

@Controller
public class CommentController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 我收到的评论
     *
     * @param model
     * @return
     */
    @GetMapping("/manage/comments")
    public ModelAndView mySendCommentList(
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "type", required = false, defaultValue = "in") String type,
            HttpSession session,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        Comment condition = new Comment();
        condition.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
        if ("in".equals(type)) {
            condition.setReplyUserId(user.getId());
            model.addAttribute("type", "in");
        } else {
            condition.setUserId(user.getId());
            model.addAttribute("type", "out");
        }
        PageVO<Comment> commentPage = commentService.findAll(condition, pageIndex, pageSize);
        for (Comment temp : commentPage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
            temp.setArticle(articleService.queryById(temp.getArticleId()));
        }
        model.addAttribute("page", commentPage);
        model.addAttribute("site_title", SiteTitleEnum.COMMENT_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/comment :: #tab-pane" : "home/userspace/comment");
    }


    /**
     * 发表评论
     *
     * @param articleId
     * @param commentId
     * @param commentContent
     * @return
     */
    @PostMapping("/comments")
    public ResponseEntity<Response> createComment(HttpSession session, Long articleId, Long replyId, Long commentId, String commentContent) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        Article article = articleService.queryById(articleId);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在!"));
        }
        try {
            //1、评论
            if (commentId == null) {
                Comment comment = new Comment();
                comment.setArticleId(article.getId());
                comment.setContent(commentContent);
                comment.setUserId(user.getId());
                comment.setReplyUserId(article.getUserId());
                commentService.insert(comment);
            } else {
                //回复
                commentService.replyComment(user.getId(), article.getId(), commentId, replyId, commentContent);
            }
            //2、修改文章的评论数目
            article.setCommentSize(commentService.countCommentSizeByArticle(article.getId()));
            articleService.update(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功!", null));
    }

    /**
     * 删除评论
     *
     * @return
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Response> delete(@PathVariable("id") Long id, HttpSession session) {
        boolean isOwner = false;
        boolean isAuthor = false;
        User user;

        Comment originalComment;
        Integer deleteCount = 1;//删除评论数

        try {
            originalComment = commentService.queryById(id);
            //如果已经到了回收站，用户不允许继续删除
            if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), originalComment.getStatus())) {
                return ResponseEntity.ok().body(new Response(false, "评论不存在！"));
            }

            user = userService.queryById(originalComment.getUserId());

            // 判断操作用户是否是评论的所有者
            User loginUser = (User) session.getAttribute("user");
            if (loginUser != null && user.getUsername().equals(loginUser.getUsername())) {
                isOwner = true;
            }
            //判断操作用户是否是文章作者
            if (Objects.equals(originalComment.getUserId(), loginUser.getId())) {
                isAuthor = true;
            }

        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "评论不存在！"));
        }

        if (!isOwner && !isAuthor) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
        }
        try {
            //1、先删除子评论
            List<Comment> commentList = commentService.findByPid(originalComment.getId());
            for (Comment comment : commentList) {
                commentService.deleteById(comment.getId());
                deleteCount++;
            }
            //2、删除该评论
            commentService.deleteById(id);
            //3、修改文章的评论数目
            Article originalArticle = articleService.queryById(originalComment.getArticleId());
            originalArticle.setCommentSize(commentService.countCommentSizeByArticle(originalArticle.getId()));
            articleService.update(originalArticle);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功", deleteCount));
    }


    /**
     * 评论置顶
     *
     * @param commentId
     * @return
     */
    @PutMapping("/comments/stick")
    public ResponseEntity<Response> stickComment(Long commentId, HttpSession session) {
        boolean isArticleAuthor = false;
        Comment originalComment = commentService.queryById(commentId);
        Article article = articleService.queryById(originalComment.getArticleId());
        if (Objects.equals(CommentStickStatusEnum.STICKY_STATUS.getCode(), originalComment.getIsSticky())) {
            return ResponseEntity.ok().body(new Response(false, "当前状态已经是置顶！"));
        }

        User loginUser = (User) session.getAttribute("user");
        if (loginUser != null && article.getUserId().equals(loginUser.getId())) {
            isArticleAuthor = true;
        }

        //只有文章作者才能置顶评论
        try {
            if (isArticleAuthor) {
                originalComment.setIsSticky(CommentStickStatusEnum.STICKY_STATUS.getCode());
                commentService.update(originalComment);
            } else {
                return ResponseEntity.ok().body(new Response(false, "没有权限操作！"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 取消评论置顶
     *
     * @param commentId
     * @return
     */
    @PutMapping("/comments/cancelStick")
    public ResponseEntity<Response> cancelStickComment(Long commentId, HttpSession session) {
        boolean isArticleAuthor = false;
        Comment originalComment = commentService.queryById(commentId);
        Article article = articleService.queryById(originalComment.getArticleId());
        if (Objects.equals(CommentStickStatusEnum.NOT_STICKY_STATUS.getCode(), originalComment.getIsSticky())) {
            return ResponseEntity.ok().body(new Response(false, "当前状态已经是未置顶！"));
        }

        User loginUser = (User) session.getAttribute("user");
        if (loginUser != null && article.getUserId().equals(loginUser.getId())) {
            isArticleAuthor = true;
        }

        //只有文章作者才能置顶评论
        try {
            if (isArticleAuthor) {
                originalComment.setIsSticky(CommentStickStatusEnum.NOT_STICKY_STATUS.getCode());
                commentService.update(originalComment);
            } else {
                return ResponseEntity.ok().body(new Response(false, "没有权限操作！"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 后台获取评论列表
     *
     * @param model
     * @return
     */
    @GetMapping("/admin/comment")

    public ModelAndView listComments(
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            Model model) {

        Comment condition = new Comment();
        condition.setContent(keywords);
        condition.setStatus(status);
        PageVO<Comment> commentPage = commentService.findAll(condition, pageIndex, pageSize);
        for (Comment temp : commentPage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
            temp.setArticle(articleService.queryById(temp.getArticleId()));
        }
        if (!async) {
            Integer publishSize = commentService.countCommentByStatus(PostStatusEnum.PUBLISH_POST.getCode());
            Integer deletedSize = commentService.countCommentByStatus(PostStatusEnum.DELETED_POST.getCode());
            Integer allSize = publishSize + deletedSize;
            model.addAttribute("publishSize", publishSize);
            model.addAttribute("deletedSize", deletedSize);
            model.addAttribute("allSize", allSize);
        }
        model.addAttribute("page", commentPage);
        model.addAttribute("status", status);
        return new ModelAndView(async == true ? "admin/comment/list :: #mainContainerReplace" : "admin/comment/list");
    }


    @DeleteMapping("/admin/comment/{id}")
    public ResponseEntity<Response> deleteCommentByAdmin(@PathVariable("id") Long id) {
        try {
            Comment originalComment = commentService.queryById(id);
            commentService.deleteById(id);

            // 修改文章的评论数目
            Article originalArticle = articleService.queryById(originalComment.getArticleId());
            if (originalArticle != null) {
                originalArticle.setCommentSize(commentService.countCommentSizeByArticle(originalArticle.getId()));
                articleService.update(originalArticle);
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 恢复评论
     *
     * @param id
     * @return
     */
    @PutMapping("/admin/comment/restore/{id}")
    public ResponseEntity<Response> restoreComment(@PathVariable("id") Long id) {
        try {
            Comment comment = commentService.queryById(id);
            comment.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            commentService.update(comment);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }
}
