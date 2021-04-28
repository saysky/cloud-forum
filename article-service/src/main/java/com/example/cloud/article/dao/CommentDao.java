package com.example.cloud.article.dao;

import com.example.cloud.article.api.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章评论表(Comment)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public interface CommentDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Comment queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Comment> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param comment 实例对象
     * @return 对象列表
     */
    List<Comment> queryAll(Comment comment);

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 影响行数
     */
    int insert(Comment comment);

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 影响行数
     */
    int update(Comment comment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    @Select("select count(*) from comment where article_id = #{articleId}")
    Integer countCommentSizeByArticle(Long articleId);

    @Select("select count(*) from comment where status = #{status}")
    Integer countCommentByStatus(String status);

    @Select("select * from comment where pid = #{pid}")
    List<Comment> findByPid(Long pid);

    @Select(value = "select max(floor) from comment where article_id = #{articleId}")
    Integer getMaxCommentFloor(Long articleId);



}