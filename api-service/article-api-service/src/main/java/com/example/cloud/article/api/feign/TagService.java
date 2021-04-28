package com.example.cloud.article.api.feign;


import com.example.cloud.article.api.entity.Tag;
import com.example.cloud.common.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章标签表(Tag)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
@FeignClient(value = "article-service")
@RestController
public interface TagService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/tag/queryById")
    Tag queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @PostMapping("/tag/queryAllByLimit")
    List<Tag> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param tag 实例对象
     * @return 实例对象
     */
    @PostMapping("/tag/insert")
    Tag insert(@RequestBody Tag tag);

    /**
     * 修改数据
     *
     * @param tag 实例对象
     * @return 实例对象
     */
    @PostMapping("/tag/update")
    Tag update(@RequestBody Tag tag);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/tag/deleteById")
    boolean deleteById(@RequestParam Long id);

    @PostMapping("/tag/findAll")
    PageVO<Tag> findAll(@RequestBody Tag condition, @RequestParam int pageNum, @RequestParam int pageSize);

    @PostMapping("/tag/findByName")
    Tag findByName(@RequestParam String name);

}