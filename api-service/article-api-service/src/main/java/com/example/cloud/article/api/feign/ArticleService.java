package com.example.cloud.article.api.feign;

import com.example.cloud.article.api.entity.Article;
import com.example.cloud.common.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章表(Article)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@FeignClient(value = "article-service")
@RestController
public interface ArticleService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/article/queryById")
    Article queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @PostMapping("/article/queryAllByLimit")
    List<Article> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param article 实例对象
     * @return 实例对象
     */
    @PostMapping("/article/insert")
    Article insert(@RequestBody Article article);

    /**
     * 修改数据
     *
     * @param article 实例对象
     * @return 实例对象
     */
    @PostMapping("/article/update")
    Article update(@RequestBody Article article);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/article/deleteById")
    boolean deleteById(@RequestParam Long id);


    /**
     * 阅读量递增
     *
     * @param id
     * @return
     */
    @PostMapping("/article/viewIncrease")
    Article viewIncrease(@RequestParam Long id);

    /**
     * 点赞
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/createZan")
    int createZan(@RequestParam Long articleId, @RequestParam Long userId);


    /**
     * 获取热门文章前十
     *
     * @return
     */
    @PostMapping("/article/listTop10HotArticles")
    List<Article> listTop10HotArticles();


    /**
     * 批量删除
     *
     * @param ids
     */
    @PostMapping("/article/batchRemove")
    void batchRemove(@RequestParam List<Long> ids);


    @PostMapping("/article/findAll")
    PageVO<Article> findAll(@RequestBody Article condition, @RequestParam int pageNum, @RequestParam int pageSize);

    @PostMapping("/article/deleteByStatus")
    void deleteByStatus(@RequestParam String status);


    /**
     * 统计不同状态的文章数
     * @param status
     * @return
     */
    @PostMapping("/article/countArticleByStatus")
    Long countArticleByStatus(@RequestParam String status);

    /**
     * 根据分类ID统计
     * @param categoryId
     * @return
     */
    @PostMapping("/article/countArticleByCategoryId")
    Long countArticleByCategoryId(@RequestParam Long categoryId);
}