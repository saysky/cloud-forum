package com.example.cloud.user.dao;

import com.example.cloud.user.api.entity.MailRetrieve;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 邮件记录表(MailRetrieve)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public interface MailRetrieveDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MailRetrieve queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<MailRetrieve> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param mailRetrieve 实例对象
     * @return 对象列表
     */
    List<MailRetrieve> queryAll(MailRetrieve mailRetrieve);

    /**
     * 新增数据
     *
     * @param mailRetrieve 实例对象
     * @return 影响行数
     */
    int insert(MailRetrieve mailRetrieve);

    /**
     * 修改数据
     *
     * @param mailRetrieve 实例对象
     * @return 影响行数
     */
    int update(MailRetrieve mailRetrieve);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    @Select("select * from mail_retrieve where account = #{account}")
    MailRetrieve findByAccount(String account);


}