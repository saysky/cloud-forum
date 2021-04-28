package com.example.cloud.web.controller.front;

import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.api.feign.CommentService;
import com.example.cloud.qa.api.feign.AnswerService;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.common.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

/**
 * 赞
 *
 * @author 言曌
 * @date 2018/5/2 下午11:34
 */

@Controller
public class VoteController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private AnswerService answerService;

    /**
     * 给文章点赞
     *
     * @param articleId
     * @return
     */
    @PostMapping("/zan/article")
    public ResponseEntity<Response> createZanForArticle(Long articleId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        try {
            int size = articleService.createZan(articleId, user.getId());
            return ResponseEntity.ok().body(new Response(true, "操作成功", size));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
    }


    /**
     * 给评论点赞
     *
     * @param commentId
     * @return
     */
    @PostMapping("/zan/comment")
    public ResponseEntity<Response> createZanForComment(Long commentId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        try {
            int size = commentService.createZan(commentId, user.getId());
            return ResponseEntity.ok().body(new Response(true, "操作成功", size));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

    }


    /**
     * 给评论点踩
     *
     * @param commentId
     * @return
     */
    @PostMapping("/cai/comment")
    public ResponseEntity<Response> createCaiForComment(Long commentId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }


        //判读是否点赞，如果点了赞，就取消
        try {
            int size = commentService.createCai(commentId, user.getId());
            return ResponseEntity.ok().body(new Response(true, "操作成功", size));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

    }

    /**
     * 给文章点赞
     *
     * @param answerId
     * @return
     */
    @PostMapping("/zan/answer")
    public ResponseEntity<Response> createZanForAnswer(Long answerId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        try {
            int size = answerService.createZan(answerId, user.getId());
            return ResponseEntity.ok().body(new Response(true, "操作成功", size));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

    }


    /**
     * 给回答点踩
     *
     * @param answerId
     * @return
     */
    @PostMapping("/cai/answer")
    public ResponseEntity<Response> createCaiForAnswer(Long answerId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        try {
            int size = answerService.createCai(answerId, user.getId());
            return ResponseEntity.ok().body(new Response(true, "操作成功", size));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
    }


}
