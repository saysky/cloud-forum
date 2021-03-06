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
 * @author θ¨ζ
 * @date 2018/5/6 δΈε9:44
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
     * ζ·»ε δΉ¦η­Ύ
     *
     * @param articleId
     * @return
     */
    @PostMapping("/bookmarks")
    public ResponseEntity<Response> createBookmark(Long articleId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "η¨ζ·ζͺη»ε½!"));
        }
        Article article = articleService.queryById(articleId);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "ζη« δΈε­ε¨οΌ"));
        }
        //2γε€ζ­ζ―ε¦ζΆθθΏδΊ
        if (bookmarkService.isMarkArticle(user.getId(), article.getId())) {
            return ResponseEntity.ok().body(new Response(false, "ζ¨ε·²η»ζΆθθΏδΊοΌθ―·ε¨η¨ζ·δΈ­εΏζ₯ηοΌ"));
        }
        try {
            //3γζ·»ε ζΆθ
            Bookmark bookmark = new Bookmark(article.getId(), user.getId());
            bookmark.setCreateTime(new Date());
            bookmarkService.insert(bookmark);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "ζΆθζεοΌθ―·ε¨η¨ζ·δΈ­εΏζ₯ηοΌ"));
    }

    /**
     * ε ι€δΉ¦η­Ύ
     *
     * @return
     */
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Response> delete(@PathVariable("id") Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "η¨ζ·ζͺη»ε½!"));
        }

        Bookmark bookmark = null;
        //1γιͺθ―δΉ¦η­Ύζ―ε¦ε­ε¨
        try {
            bookmark = bookmarkService.queryById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "δΉ¦η­ΎδΈε­ε¨οΌ"));
        }

        //2γιͺθ―ζ―ε¦ζ―θ―₯η¨ζ·η
        if (Objects.equals(bookmark.getUserId(), user.getId())) {
            bookmarkService.deleteById(bookmark.getId());
        } else {
            return ResponseEntity.ok().body(new Response(false, "ζ²‘ζζιζδ½οΌ"));
        }

        return ResponseEntity.ok().body(new Response(true, "ε€ηζε"));
    }
}
