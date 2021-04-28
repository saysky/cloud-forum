package com.example.cloud.article.api.feign;

import com.example.cloud.article.api.entity.Article;
import com.example.cloud.article.api.entity.Category;
import com.example.cloud.article.api.entity.Comment;
import com.example.cloud.common.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@FeignClient(value = "article-service")
@RestController
public interface CommentService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/comment/queryById")
    Comment queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @PostMapping("/comment/queryAllByLimit")
    List<Comment> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @PostMapping("/comment/insert")
    Comment insert(@RequestBody Comment comment);

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @PostMapping("/comment/update")
    Comment update(@RequestBody Comment comment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/comment/deleteById")
    boolean deleteById(@RequestParam Long id);


    /**
     * 点赞
     *
     * @param commentId
     * @return
     */
    @PostMapping("/comment/createZan")
    int createZan(@RequestParam Long commentId, @RequestParam Long userId);

    /**
     * 点踩
     *
     * @param commentId
     * @return
     */
    @PostMapping("/comment/createCai")
    int createCai(@RequestParam Long commentId, @RequestParam Long userId);


    @PostMapping("/comment/findAll")
    PageVO<Comment> findAll(@RequestBody Comment condition, @RequestParam int pageNum, @RequestParam int pageSize);

    /**
     * 统计某篇文章的评论数
     *
     * @param articleId
     * @return
     */
    @PostMapping("/comment/countCommentSizeByArticle")
    Integer countCommentSizeByArticle(@RequestParam Long articleId);


    /**
     * 根据状态统计评论数
     *
     * @param status
     * @return
     */
    @PostMapping("/comment/countCommentByStatus")
    Integer countCommentByStatus(@RequestParam String status);

    @PostMapping("/comment/findByPid")
    List<Comment> findByPid(@RequestParam Long pid);

    /**
     * 回复评论
     *
     * @param commentId
     * @param articleId
     * @param commentContent
     * @return
     */
    @PostMapping("/comment/replyComment")
    void replyComment(@RequestParam Long userId ,@RequestParam Long articleId,
                      @RequestParam Long commentId,
                      @RequestParam(required = false) Long replyId,
                      @RequestParam String commentContent);

}