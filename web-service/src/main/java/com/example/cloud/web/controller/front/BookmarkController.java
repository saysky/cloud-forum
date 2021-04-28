package com.example.cloud.web.controller.front;

import com.example.cloud.article.api.entity.Article;
import com.example.cloud.article.api.entity.Bookmark;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.api.feign.BookmarkService;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.common.enums.SiteTitleEnum;
import com.example.cloud.common.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.Objects;


/**
 * @author 言曌
 * @date 2018/5/6 下午9:44
 */

@Controller

public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;


    /**
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/manage/bookmarks")
    public ModelAndView list(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                             @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                             HttpSession session,
                             Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        Bookmark condition = new Bookmark();
        condition.setUserId(user.getId());
        PageVO<Bookmark> bookmarkPage = bookmarkService.findAll(condition, pageIndex, pageSize);
        for (Bookmark temp : bookmarkPage.getContent()) {
            temp.setArticle(articleService.queryById(temp.getArticleId()));
        }
        model.addAttribute("page", bookmarkPage);
        model.addAttribute("site_title", SiteTitleEnum.BOOKMARK_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/bookmark :: #right-box-body-replace" : "home/userspace/bookmark");
    }

    /**
     * 添加书签
     *
     * @param articleId
     * @return
     */
    @PostMapping("/bookmarks")
    public ResponseEntity<Response> createBookmark(Long articleId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        Article article = articleService.queryById(articleId);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在！"));
        }
        //2、判断是否收藏过了
        if (bookmarkService.isMarkArticle(user.getId(), article.getId())) {
            return ResponseEntity.ok().body(new Response(false, "您已经收藏过了，请在用户中心查看！"));
        }
        try {
            //3、添加收藏
            Bookmark bookmark = new Bookmark(article.getId(), user.getId());
            bookmark.setCreateTime(new Date());
            bookmarkService.insert(bookmark);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "收藏成功，请在用户中心查看！"));
    }

    /**
     * 删除书签
     *
     * @return
     */
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Response> delete(@PathVariable("id") Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        Bookmark bookmark = null;
        //1、验证书签是否存在
        try {
            bookmark = bookmarkService.queryById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "书签不存在！"));
        }

        //2、验证是否是该用户的
        if (Objects.equals(bookmark.getUserId(), user.getId())) {
            bookmarkService.deleteById(bookmark.getId());
        } else {
            return ResponseEntity.ok().body(new Response(false, "没有权限操作！"));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }
}
