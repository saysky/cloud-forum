package com.example.cloud.article.dao;

import com.example.cloud.article.api.entity.ArticleZan;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章和赞关联表(ArticleZan)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-30 14:44:35
 */
public interface ArticleZanDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ArticleZan queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ArticleZan> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param articleZan 实例对象
     * @return 对象列表
     */
    List<ArticleZan> queryAll(ArticleZan articleZan);

    /**
     * 新增数据
     *
     * @param articleZan 实例对象
     * @return 影响行数
     */
    int insert(ArticleZan articleZan);

    /**
     * 修改数据
     *
     * @param articleZan 实例对象
     * @return 影响行数
     */
    int update(ArticleZan articleZan);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);



    /**
     * 根据用户ID和文章ID查询
     *
     * @param userId
     * @param articleId
     * @return
     */
    @Select("select * from article_zan where user_id = #{userId} AND article_id = #{articleId} limit 1")
    ArticleZan queryByUserIdAndArticleId(@Param("userId") Long userId,
                                         @Param("articleId") Long articleId);

}