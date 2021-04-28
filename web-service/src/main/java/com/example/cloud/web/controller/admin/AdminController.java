package com.example.cloud.web.controller.admin;

import com.example.cloud.user.api.entity.Slide;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.SlideService;
import com.example.cloud.common.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author 言曌
 * @date 2018/3/19 下午11:21
 */

@Controller
@RequestMapping("/admin")

public class AdminController {

    @Autowired
    private SlideService slideService;

    /**
     * 获取后台管理主页面
     *
     * @return
     */
    @GetMapping
    public String index() {
//        return "admin/index";
        return "forward:/admin/user";
    }


    @GetMapping("/settings")
    public String settings() {
        return "forward:/admin/settings/slide";
    }

    /**
     * 幻灯片设置页面
     *
     * @return
     */
    @GetMapping("/settings/slide")
    public String slidePage(Model model) {
        List<Slide> slideList = slideService.queryAllByLimit(0,100);
        model.addAttribute("slideList", slideList);
        return "admin/settings/slide";
    }

    @GetMapping("/settings/slide/{id}")
    public String slidePage(@PathVariable("id") Long id, Model model) {
        Slide slide = slideService.queryById(id);
        model.addAttribute("slide", slide);
        return "admin/settings/slide-edit";
    }


    /**
     * 添加幻灯片提交
     *
     * @param slide
     * @return
     */
    @PostMapping("/settings/slide")
    public ResponseEntity<Response> submitSlide(@RequestBody Slide slide) {
        try {
            slideService.update(slide);
        }  catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功"));
    }


    /**
     * @param id
     * @return
     */
    @DeleteMapping("/settings/slide/{id}")
    public ResponseEntity<Response> deleteTag(@PathVariable("id") Long id) {
        try {
            slideService.deleteById(id);
        }  catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "删除成功"));
    }
}
