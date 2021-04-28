package com.example.cloud.article.dao;

import com.example.cloud.article.api.entity.Bookmark;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收藏表(Bookmark)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public interface BookmarkDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Bookmark queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Bookmark> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param bookmark 实例对象
     * @return 对象列表
     */
    List<Bookmark> queryAll(Bookmark bookmark);

    /**
     * 新增数据
     *
     * @param bookmark 实例对象
     * @return 影响行数
     */
    int insert(Bookmark bookmark);

    /**
     * 修改数据
     *
     * @param bookmark 实例对象
     * @return 影响行数
     */
    int update(Bookmark bookmark);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    @Select("select * from bookmark where user_id = #{userId} AND article_id = #{articleId}")
    List<Bookmark> findByUserIdAndArticleId(@Param("userId") Long userId, @Param("articleId") Long articleId);

}