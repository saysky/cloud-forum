package com.example.cloud.article.api.feign;

import com.example.cloud.article.api.entity.Bookmark;
import com.example.cloud.article.api.entity.Category;
import com.example.cloud.article.api.entity.Comment;
import com.example.cloud.common.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (Category)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@FeignClient(value = "article-service")
@RestController
public interface CategoryService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/category/queryById")
    Category queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @PostMapping("/category/queryAllByLimit")
    List<Category> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param category 实例对象
     * @return 实例对象
     */
    @PostMapping("/category/insert")
    Category insert(@RequestBody Category category);

    /**
     * 修改数据
     *
     * @param category 实例对象
     * @return 实例对象
     */
    @PostMapping("/category/update")
    Category update(@RequestBody Category category);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/category/deleteById")
    boolean deleteById(@RequestParam Long id);


    @PostMapping("/category/findAll")
    PageVO<Category> findAll(@RequestBody Category condition, @RequestParam int pageNum, @RequestParam int pageSize);

    @PostMapping("/category/findByUserIdAndName")
    List<Category> findByUserIdAndName(@RequestParam Long userId, @RequestParam String name);

    @PostMapping("/category/findByUserId")
    List<Category> findByUserId(@RequestParam Long userId);


    /**
     * 向上移动
     *
     * @param currentId 当前分类的id
     * @param otherId   另一个要交换的分类的id
     */
    @PostMapping("/category/changePriority")
    void changePriority(@RequestParam Long userId, @RequestParam Long currentId, @RequestParam Long otherId);

}