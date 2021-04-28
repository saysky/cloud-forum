package com.example.cloud.qa.dao;

import com.example.cloud.qa.api.entity.AnswerCai;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 回答和踩关联表(AnswerCai)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-30 14:44:35
 */
public interface AnswerCaiDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    AnswerCai queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AnswerCai> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param answerCai 实例对象
     * @return 对象列表
     */
    List<AnswerCai> queryAll(AnswerCai answerCai);

    /**
     * 新增数据
     *
     * @param answerCai 实例对象
     * @return 影响行数
     */
    int insert(AnswerCai answerCai);

    /**
     * 修改数据
     *
     * @param answerCai 实例对象
     * @return 影响行数
     */
    int update(AnswerCai answerCai);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据用户ID和回答ID查询
     *
     * @param userId
     * @param answerId
     * @return
     */
    @Select("select * from answer_cai where user_id = #{userId} AND answer_id = #{answerId} limit 1")
    AnswerCai queryByUserIdAndAnswerId(@Param("userId") Long userId,
                                         @Param("answerId") Long answerId);


}