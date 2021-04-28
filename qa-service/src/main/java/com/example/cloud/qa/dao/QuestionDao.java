package com.example.cloud.qa.dao;

import com.example.cloud.qa.api.entity.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 提问表(Question)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public interface QuestionDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Question queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Question> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param question 实例对象
     * @return 对象列表
     */
    List<Question> queryAll(Question question);

    /**
     * 新增数据
     *
     * @param question 实例对象
     * @return 影响行数
     */
    int insert(Question question);

    /**
     * 修改数据
     *
     * @param question 实例对象
     * @return 影响行数
     */
    int update(Question question);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);


    @Update("update question set view_size = view_size + 1 where id = #{id}")
    void viewIncrease(Long id);


}