package com.example.cloud.web.controller.admin;

import com.example.cloud.article.api.entity.Tag;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.api.feign.TagService;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.common.vo.Response;
import com.example.cloud.qa.api.feign.QuestionService;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/5/24 下午11:08
 */

@Controller
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/admin/tag")
    public ModelAndView index(Model model,
                              @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                              @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageVO<Tag> tagList = tagService.findAll(new Tag(), pageIndex, pageSize);
        model.addAttribute("page", tagList);
        return new ModelAndView(async == true ? "admin/tag/list :: #mainContainerReplace" : "admin/tag/list");
    }


    @GetMapping("/admin/tag/edit/{id}")
    public ModelAndView editTag(Model model,
                                @PathVariable(value = "id") Long id,
                                @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) throws NotFoundException {

        Tag tag = tagService.queryById(id);
        if (tag == null) {
            throw new NotFoundException("标签不存在");
        }
        if (async == false) {
            PageVO<Tag> tagList = tagService.findAll(new Tag(), pageIndex, pageSize);
            model.addAttribute("page", tagList);
        }
        model.addAttribute("tag", tag);
        return new ModelAndView(async == true ? "admin/tag/edit :: #left-box-body-replace" : "admin/tag/edit");
    }


    /**
     * 创建
     *
     * @param tag
     * @return
     */
    @PostMapping("/admin/tag")
    public ResponseEntity<Response> create(Tag tag) {
        Tag temp = tagService.findByName(tag.getName());
        //查找tag是否存在
        if (temp != null) {
            if (tag.getId() == null || !Objects.equals(tag.getId(), temp.getId())) {
                return ResponseEntity.ok().body(new Response(false, "标签已存在"));
            }
        }
        try {
            if (tag.getGuid() == null || "".equals(tag.getGuid())) {
                tag.setGuid("/article?keywords=" + tag.getName());
            }
            if(tag.getId() == null) {
                tagService.insert(tag);
            } else {
                tagService.update(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "添加成功"));
    }


    /**
     * @param id
     * @return
     */
    @DeleteMapping("/admin/tag/{id}")
    public ResponseEntity<Response> deleteTag(@PathVariable("id") Long id) {
        try {
            tagService.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "删除成功"));
    }


}
