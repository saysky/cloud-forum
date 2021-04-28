package com.example.cloud.qa.dao;

import com.example.cloud.qa.api.entity.AnswerCai;
import com.example.cloud.qa.api.entity.AnswerZan;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 回答和赞关联表(AnswerZan)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-30 14:44:35
 */
public interface AnswerZanDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    AnswerZan queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AnswerZan> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param answerZan 实例对象
     * @return 对象列表
     */
    List<AnswerZan> queryAll(AnswerZan answerZan);

    /**
     * 新增数据
     *
     * @param answerZan 实例对象
     * @return 影响行数
     */
    int insert(AnswerZan answerZan);

    /**
     * 修改数据
     *
     * @param answerZan 实例对象
     * @return 影响行数
     */
    int update(AnswerZan answerZan);

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
    @Select("select * from answer_zan where user_id = #{userId} AND answer_id = #{answerId} limit 1")
    AnswerZan queryByUserIdAndAnswerId(@Param("userId") Long userId,
                                       @Param("answerId") Long answerId);

}