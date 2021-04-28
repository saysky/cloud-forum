package com.example.cloud.web.controller.front;

import com.example.cloud.common.enums.SiteTitleEnum;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.common.vo.Response;
import com.example.cloud.user.api.entity.Relationship;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.RelationshipService;
import com.example.cloud.user.api.feign.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/5/2 下午3:54
 */

@Controller

public class RelationshipController {

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private UserService userService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 关注
     *
     * @param uid
     * @return
     */
    @PostMapping("/relationships/follow")
    public ResponseEntity<Response> follow(Long uid, HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
        User user = userService.queryById(uid);
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户不存在！"));
        }
        if (Objects.equals(uid, loginUser.getId())) {
            return ResponseEntity.ok().body(new Response(false, "您时时刻刻都在关注自己哦！"));
        }
        //1、添加关系
        Relationship relationship = new Relationship(uid, loginUser.getId());
        relationshipService.saveRelationship(relationship);
        return ResponseEntity.ok().body(new Response(true, "操作成功"));
    }

    /**
     * 取消关注
     *
     * @param uid
     * @return
     */
    @PostMapping("/relationships/notfollow")
    public ResponseEntity<Response> notfollow(Long uid, HttpSession session) {
        User loginUser = (User) session.getAttribute("user");

        User user = userService.queryById(uid);
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户不存在！"));

        }
        Relationship relationship = new Relationship(uid, loginUser.getId());
        relationshipService.removeRelationship(relationship);

        return ResponseEntity.ok().body(new Response(true, "操作成功"));

    }


//    用户中心

    @GetMapping("/manage/relationships")
    public String relationships() {
        return "forward:/manage/relationships/follows";
    }

    //粉丝-关注 start

    /**
     * 查看所有的关注者
     *
     * @param async
     * @param page
     * @param size
     * @param model
     * @return
     */
    @GetMapping("/manage/relationships/follows")
    public ModelAndView follows(
            @RequestParam(value = "async", required = false) boolean async,
            HttpSession session,
            @RequestParam(value = "pageIndex", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "12", required = false) Integer size,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        PageVO<User> userPage = relationshipService.listFollows(user.getId(), page, size);

        List<Long> friendIds = relationshipService.listFriends(user.getId());
        List<User> userList = userPage.getContent();
        for (int i = 0; i < userList.size(); i++) {
            if (friendIds.contains(userList.get(i).getId())) {
                userPage.getContent().get(i).setIsFriend(2);
            }
        }
        model.addAttribute("page", userPage);
        model.addAttribute("dataType", "follows");
        model.addAttribute("site_title", SiteTitleEnum.RELATIONSHIP_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/relationship :: #right-box-body-replace" : "home/userspace/relationship");

    }

    /**
     * 查看所有的粉丝
     *
     * @param async
     * @param page
     * @param size
     * @param model
     * @return
     */
    @GetMapping("/manage/relationships/fans")
    public ModelAndView fans(
            HttpSession session,
            @RequestParam(value = "async", required = false, defaultValue = "false") boolean async,
            @RequestParam(value = "pageIndex", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "12", required = false) Integer size,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        PageVO<User> userPage = relationshipService.listFans(user.getId(), page, size);
        List<Long> friendIds = relationshipService.listFriends(user.getId());
        List<User> userList = userPage.getContent();
        for (int i = 0; i < userList.size(); i++) {
            if (friendIds.contains(userList.get(i).getId())) {
                userPage.getContent().get(i).setIsFriend(2);
            }
        }
        model.addAttribute("page", userPage);
        model.addAttribute("dataType", "fans");
        model.addAttribute("site_title", SiteTitleEnum.RELATIONSHIP_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/relationship :: #right-box-body-replace" : "home/userspace/relationship");

    }


    /**
     * 关注或取消关注
     *
     * @param userId
     * @param optType
     * @return
     */
    @PostMapping("/manage/relationships")
    public ResponseEntity<Response> followUser(Long userId, String optType, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        //1、判断用户是否存在
        User temp = userService.queryById(userId);
        if (temp == null) {
            return ResponseEntity.ok().body(new Response(false, "用户不存在"));
        }
        //2、判断是关注还是取消关注
        //关注
        if ("follow".equals(optType)) {
            relationshipService.saveRelationship(new Relationship( userId,user.getId()));
        } else if ("notfollow".equals(optType)) {
            //取消关注
            relationshipService.removeRelationship(new Relationship( userId,user.getId()));
        } else {
            //非法操作
            return ResponseEntity.ok().body(new Response(false, "非法操作"));
        }
        Long fanSize = userService.queryById(userId).getFanSize();
        return ResponseEntity.ok().body(new Response(true, "操作成功", fanSize));
    }


}