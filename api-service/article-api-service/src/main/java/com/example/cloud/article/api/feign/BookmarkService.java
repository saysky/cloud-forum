package com.example.cloud.article.api.feign;

import com.example.cloud.article.api.entity.Bookmark;
import com.example.cloud.common.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏表(Bookmark)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@FeignClient(value = "article-service")
@RestController
public interface BookmarkService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/bookmark/queryById")
    Bookmark queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @PostMapping("/bookmark/queryAllByLimit")
    List<Bookmark> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param bookmark 实例对象
     * @return 实例对象
     */
    @PostMapping("/bookmark/insert")
    Bookmark insert(@RequestBody Bookmark bookmark);

    /**
     * 修改数据
     *
     * @param bookmark 实例对象
     * @return 实例对象
     */
    @PostMapping("/bookmark/update")
    Bookmark update(@RequestBody Bookmark bookmark);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/bookmark/deleteById")
    boolean deleteById(@RequestParam Long id);

    /**
     * 是否收藏了某篇文章
     * @param userId
     * @param articleId
     * @return
     */
    @PostMapping("/bookmark/isMarkArticle")
    boolean isMarkArticle(@RequestParam Long userId, @RequestParam Long articleId);

    @PostMapping("/bookmark/findAll")
    PageVO<Bookmark> findAll(@RequestBody Bookmark condition, @RequestParam int pageNum, @RequestParam int pageSize);


}