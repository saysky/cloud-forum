package com.example.cloud.web.controller.front;

import com.example.cloud.article.api.entity.Category;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.api.feign.CategoryService;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/5/7 下午3:52
 */

@Controller
public class CategoryController {


    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;


//用户中心

    /**
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/manage/categorys")
    public ModelAndView listCategorys(@RequestParam(value = "async", required = false, defaultValue = "false") boolean async,
                                      @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                      HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        Category condition = new Category();
        condition.setUserId(user.getId());
        PageVO<Category> categoryPage = categoryService.findAll(condition, pageIndex, pageSize);
        for (Category temp : categoryPage.getContent()) {
            temp.setArticleSize(articleService.countArticleByCategoryId(temp.getId()));
        }
        model.addAttribute("page", categoryPage);
        model.addAttribute("site_title", SiteTitleEnum.CATEGORY_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/category :: #right-box-body-replace" : "home/userspace/category");
    }

    /**
     * 创建
     * 限制创建100个
     *
     * @param categoryName
     * @return
     */
    @PostMapping("/category")
    public ResponseEntity<Response> create(HttpSession session, String categoryName) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        String name = categoryName.trim();
        //1、判断长度和排除空格
        if (name.length() < 1 || name.length() > 20) {
            return ResponseEntity.ok().body(new Response(false, "分类名长度不合法"));
        }
        //2、如果该分类名已存在，禁止创建
        List<Category> categoryList = categoryService.findByUserIdAndName(user.getId(), name);
        if (categoryList.size() != 0) {
            return ResponseEntity.ok().body(new Response(false, "分类名已存在"));
        }

        Category result = null;
        Category category = new Category();
        category.setName(name);
        category.setUserId(user.getId());
        try {
            result = categoryService.insert(category);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "添加成功", result));
    }

    /**
     * 删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping("/category/{id}")
    public ResponseEntity<Response> deleteCategory(HttpSession session, @PathVariable("id") Long id) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        Category category = null;
        //1、判断分类不为空
        try {
            category = categoryService.queryById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok().body(new Response(false, "分类不存在"));

        }
        //2、判断分类是否属于该用户
        if (!Objects.equals(category.getUserId(), user.getId())) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
        }

        //3、删除分类
        categoryService.deleteById(category.getId());
        return ResponseEntity.ok().body(new Response(true, "删除成功"));
    }


    /**
     * 更新
     * 限制创建100个
     *
     * @param categoryName
     * @return
     */
    @PutMapping("/category/{id}/name")
    public ResponseEntity<Response> updateCategoryName(HttpSession session, @PathVariable("id") Long categoryId, String categoryName) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        Category category = null;
        String name = categoryName.trim();
        //1、判断长度和排除空格
        if (name.length() < 1 || name.length() > 20) {
            return ResponseEntity.ok().body(new Response(false, "分类名长度不合法"));
        }
        //2、判断分类不为空
        try {
            category = categoryService.queryById(categoryId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok().body(new Response(false, "分类不存在"));
        }
        //3、判断分类是否属于该用户
        if (!category.getUserId().equals(user.getId())) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
        }
        //4、如果该分类名已存在，禁止创建
        List<Category> categoryList = categoryService.findByUserIdAndName(user.getId(), name);
        if (categoryList.size() != 0) {
            return ResponseEntity.ok().body(new Response(false, "分类名已存在"));
        }
        //6、修改分类
        category.setName(name);
        categoryService.update(category);
        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }


    /**
     * 更新是否隐藏
     * 限制创建100个
     *
     * @return
     */
    @PutMapping("/category/{id}/isHidden")
    public ResponseEntity<Response> updateCategoryIsHidden(HttpSession session, @PathVariable("id") Long categoryId, String isHidden) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        Category category = null;
        //1、判断分类不为空
        try {
            category = categoryService.queryById(categoryId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok().body(new Response(false, "分类不存在"));
        }
        //2、判断分类是否属于该用户
        if (!category.getUserId().equals(user.getId())) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
        }

        //3、修改分类
        category.setIsHidden(isHidden);
        categoryService.update(category);
        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }

    /**
     * 更新位置
     * 限制创建100个
     *
     * @return
     */
    @PutMapping("/category/position")
    public ResponseEntity<Response> updateCategoryPosition(HttpSession session,
                                                           @RequestParam("upId") Long upId,
                                                           @RequestParam("currentId") Long currentId,
                                                           @RequestParam("downId") Long downId) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        //向上移动
        if (upId != null && currentId != null) {
            categoryService.changePriority(user.getId(), currentId, upId);
            return ResponseEntity.ok().body(new Response(true, "操作成功"));
        }

        //向下移动
        if (downId != null && currentId != null) {
            categoryService.changePriority(user.getId(), currentId, downId);
        }

        return ResponseEntity.ok().body(new Response(true, "操作成功"));

    }
}
