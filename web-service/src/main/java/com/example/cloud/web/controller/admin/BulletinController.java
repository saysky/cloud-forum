package com.example.cloud.web.controller.admin;

import com.example.cloud.common.enums.PostStatusEnum;
import com.example.cloud.common.enums.SiteTitleEnum;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.common.vo.Response;
import com.example.cloud.user.api.entity.Bulletin;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.BulletinService;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.web.controller.common.BaseController;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/6/10 上午11:25
 */
@Controller
public class BulletinController extends BaseController {

    @Autowired
    private BulletinService bulletinService;

    @Autowired
    private UserService userService;


    /**
     * 公告列表显示
     *
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/bulletins")
    public ModelAndView frontbulletinList(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                          @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {
        Bulletin condition = new Bulletin();
        condition.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
        PageVO<Bulletin> bulletinPage = bulletinService.findAll(condition, pageIndex, pageSize);
        model.addAttribute("page", bulletinPage);
        model.addAttribute("site_title", SiteTitleEnum.bulletin_HOME.getTitle());

        return new ModelAndView(async == true ? "home/bulletin_list :: #bulletin-box-replace" : "home/bulletin_list");
    }


    /**
     * 获得公告创建页面
     *
     * @return
     */
    @GetMapping("/admin/bulletin/new")
    public String bulletinNew() {
        return "admin/bulletin/add";
    }


    /**
     * 获得公告修改页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/admin/bulletin/edit/{id}")

    public String bulletinEdit(@PathVariable(value = "id") Long id, Model model) throws NotFoundException {
        Bulletin bulletin = bulletinService.queryById(id);
        //1、判断公告是否存在
        if (bulletin == null) {
            throw new NotFoundException("公告不存在!");
        }

        //2、判断是否可以访问
        if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), bulletin.getStatus())) {
            throw new NotFoundException("该公告已被管理员删除!");
        }

        model.addAttribute("bulletin", bulletin);
        return "admin/bulletin/edit";
    }

    /**
     * 获得公告详情页面
     *
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/bulletins/{name}")
    public ModelAndView bulletinDetailByKey(@PathVariable(value = "name") String name,
                                            HttpSession session,
                                            Model model) throws NotFoundException {
        Bulletin bulletin = bulletinService.findByName(name);
        //1、判断公告是否存在
        if (bulletin == null) {
            throw new NotFoundException("公告不存在!");
        }

        //2、判断是否可以访问
        if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), bulletin.getStatus())) {
            throw new NotFoundException("该公告已被管理员删除!");
        }

        // 3、判断操作用户是否是公告的所有者
        User loginUser = (User) session.getAttribute("user");
        boolean isOwner = false;
        if (loginUser != null) {
            isOwner = Objects.equals(loginUser.getId(), bulletin.getUserId());
        }
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("bulletin", bulletin);
        return new ModelAndView("home/bulletin_detail");
    }


    /**
     * 发布公告提交
     *
     * @param bulletin
     * @return
     */
    @PostMapping("/admin/bulletin")
    public ResponseEntity<Response> savebulletin(@RequestBody Bulletin bulletin) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        user = userService.findByUsername(user.getUsername());
        String guid = null;

        try {
            //1、 判断是修改还是新增
            if (bulletin.getId() != null) {
                //修改
                Bulletin originalbulletin = bulletinService.queryById(bulletin.getId());
                originalbulletin.setTitle(bulletin.getTitle());
                originalbulletin.setContent(bulletin.getContent());
                originalbulletin.setPosition(bulletin.getPosition());
                originalbulletin.setStatus(bulletin.getStatus());
                originalbulletin.setIsSticky(bulletin.getIsSticky());
                originalbulletin.setName(bulletin.getName());
                bulletinService.update(originalbulletin);
                guid = originalbulletin.getGuid();
            } else {
                //新增
                bulletin.setUserId(user.getId());
                bulletin.setCreateTime(new Date());
                bulletin.setGuid("/bulletins/" + bulletin.getName());
                Bulletin result = bulletinService.insert(bulletin);
                guid = result.getGuid();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功", guid));
    }

    /**
     * 后台公告列表显示
     *
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/admin/bulletin")
    public ModelAndView adminbulletinList(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                          @RequestParam(value = "status", required = false, defaultValue = "publish") String status,
                                          @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {
        Bulletin condition = new Bulletin();
        condition.setStatus(status);
        PageVO<Bulletin> bulletinPage = bulletinService.findAll(condition, pageIndex, pageSize);
        for (Bulletin temp : bulletinPage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        model.addAttribute("page", bulletinPage);
        model.addAttribute("site_title", SiteTitleEnum.bulletin_ADMIN.getTitle());
        return new ModelAndView(async == true ? "admin/bulletin/list :: #tab-pane" : "admin/bulletin/list");
    }


    /**
     * 删除公告
     *
     * @param id
     * @return
     */
    @DeleteMapping("/admin/bulletin/{id}")

    public ResponseEntity<Response> deleteTag(@PathVariable("id") Long id) {
        try {
            bulletinService.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "删除成功"));
    }
}
