package com.example.cloud.web.controller.admin;

import com.example.cloud.common.enums.UserStatusEnum;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.common.vo.Response;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;


/**
 * @author 言曌
 * @date 2018/3/20 下午3:32
 */

@RestController
@RequestMapping("/admin/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    //未设置职业的值
    private static final Long JOB_NOTSET_ID = 1L;

    /**
     * 查询所用用户
     *
     * @return
     */
    @GetMapping
    public ModelAndView list(
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "orderby", required = false, defaultValue = "createTime") String orderby,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        User condition = new User();
        condition.setUsername(keywords);
        PageVO<User> userList = userService.findAll(condition, pageIndex, pageSize);
        model.addAttribute("page", userList);
        model.addAttribute("orderby", orderby);
        return new ModelAndView(async == true ? "admin/user/list :: #mainContainerReplace" : "admin/user/list");
    }


    /**
     * 获取添加用户页面
     *
     * @return
     */
    @GetMapping("/add")
    public ModelAndView addUserForm() {
        return new ModelAndView("admin/user/add");
    }

    /**
     * 获取修改用户页面
     *
     * @param id
     * @return
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editUserForm(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("admin/user/edit");
        User user = userService.queryById(id);
        modelAndView.addObject("user", user);
        return modelAndView;
    }


    /**
     * 获得某个用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ModelAndView get(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("admin/user/profile");
        User user = userService.queryById(id);
        //1、判断用户是否存在
        if (user != null) {
            modelAndView.addObject("user", user);
        } else {
            modelAndView.addObject("userNotExist", true);
            modelAndView.addObject("errorMsg", "用户不存在");
        }
        return modelAndView;
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 添加/更新用户
     *
     * @param user
     * @return
     */
    @PostMapping
    public ResponseEntity<Response> saveUser(User user) {
        if (user.getId() == null) {
            user.setAnswerSize(0L);
            user.setNickname(user.getUsername());
            user.setAvatar("/img/default-avatar.jpg");
            user.setCreateTime(new Date());
            user.setFanSize(0L);
            user.setFollowSize(0L);
            user.setViewSize(0L);
            user.setQuestionSize(0L);
            user.setArticleSize(0L);
            user.setAnswerSize(0L);
            user.setStatus(UserStatusEnum.NORMAL.getCode());
            user.setJobId(JOB_NOTSET_ID);
            userService.insert(user);
        } else {
            userService.update(user);
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功", user));
    }


}
