package com.example.cloud.article.dao;

import com.example.cloud.article.api.entity.CommentCai;
import com.example.cloud.article.api.entity.CommentZan;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论和点赞关联表(CommentZan)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-30 14:44:35
 */
public interface CommentZanDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CommentZan queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<CommentZan> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param commentZan 实例对象
     * @return 对象列表
     */
    List<CommentZan> queryAll(CommentZan commentZan);

    /**
     * 新增数据
     *
     * @param commentZan 实例对象
     * @return 影响行数
     */
    int insert(CommentZan commentZan);

    /**
     * 修改数据
     *
     * @param commentZan 实例对象
     * @return 影响行数
     */
    int update(CommentZan commentZan);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据用户ID和评论ID查询
     *
     * @param userId
     * @param commentId
     * @return
     */
    @Select("select * from comment_zan where user_id = #{userId} AND comment_id = #{commentId} limit 1")
    CommentZan queryByUserIdAndCommentId(@Param("userId") Long userId,
                                         @Param("commentId") Long commentId);

}