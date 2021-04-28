package com.example.cloud.user.dao;

import com.example.cloud.user.api.entity.Bulletin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 公告表(Bulletin)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public interface BulletinDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Bulletin queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Bulletin> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param bulletin 实例对象
     * @return 对象列表
     */
    List<Bulletin> queryAll(Bulletin bulletin);

    /**
     * 新增数据
     *
     * @param bulletin 实例对象
     * @return 影响行数
     */
    int insert(Bulletin bulletin);

    /**
     * 修改数据
     *
     * @param bulletin 实例对象
     * @return 影响行数
     */
    int update(Bulletin bulletin);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    @Select("select * from bulletin where name = #{name} limit 1")
    Bulletin findByName(String name);


}