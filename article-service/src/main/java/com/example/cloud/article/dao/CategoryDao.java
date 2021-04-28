package com.example.cloud.article.dao;

import com.example.cloud.article.api.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (Category)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public interface CategoryDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Category queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Category> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param category 实例对象
     * @return 对象列表
     */
    List<Category> queryAll(Category category);

    /**
     * 新增数据
     *
     * @param category 实例对象
     * @return 影响行数
     */
    int insert(Category category);

    /**
     * 修改数据
     *
     * @param category 实例对象
     * @return 影响行数
     */
    int update(Category category);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    @Select("select * from category where user_id = #{userId} AND name = #{name}")
    List<Category> findByUserIdAndName(@Param("userId") Long userId,
                                       @Param("name") String name);


    @Select("select * from category where user_id = #{userId} order by position DESC")
    List<Category> findByUserId(@Param("userId") Long userId);


}