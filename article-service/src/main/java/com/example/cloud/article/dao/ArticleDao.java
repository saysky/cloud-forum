package com.example.cloud.article.dao;

import com.example.cloud.article.api.entity.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章表(Article)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public interface ArticleDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Article queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Article> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param article 实例对象
     * @return 对象列表
     */
    List<Article> queryAll(Article article);

    /**
     * 新增数据
     *
     * @param article 实例对象
     * @return 影响行数
     */
    int insert(Article article);

    /**
     * 修改数据
     *
     * @param article 实例对象
     * @return 影响行数
     */
    int update(Article article);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 获取最热门的10个文章
     * @return
     */
    List<Article> findTop10HotArticle();

    @Delete("delete from article where status = #{status}")
    int deleteByStatus(String status);

    @Select("select count(*) from article where status = #{status}")
    Long countArticleByStatus(String status);

    @Select("select count(*) from article where category_id = #{categoryId}")
    Long countArticleByCategoryId(Long categoryId);


}