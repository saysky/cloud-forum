package com.example.cloud.article.dao;

import com.example.cloud.article.api.entity.ArticleZan;
import com.example.cloud.article.api.entity.CommentCai;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论和踩关联表(CommentCai)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-30 14:44:35
 */
public interface CommentCaiDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CommentCai queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<CommentCai> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param commentCai 实例对象
     * @return 对象列表
     */
    List<CommentCai> queryAll(CommentCai commentCai);

    /**
     * 新增数据
     *
     * @param commentCai 实例对象
     * @return 影响行数
     */
    int insert(CommentCai commentCai);

    /**
     * 修改数据
     *
     * @param commentCai 实例对象
     * @return 影响行数
     */
    int update(CommentCai commentCai);

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
    @Select("select * from comment_cai where user_id = #{userId} AND comment_id = #{commentId} limit 1")
    CommentCai queryByUserIdAndCommentId(@Param("userId") Long userId,
                                         @Param("commentId") Long commentId);


}