package com.example.cloud.user.dao;

import com.example.cloud.user.api.entity.Relationship;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 粉丝关系表(Relationship)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
public interface RelationshipDao {

    /**
     * 通过ID查询单条数据
     *
     * @param toUserId 主键
     * @return 实例对象
     */
    Relationship queryById(Long toUserId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Relationship> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param relationship 实例对象
     * @return 对象列表
     */
    List<Relationship> queryAll(Relationship relationship);

    /**
     * 新增数据
     *
     * @param relationship 实例对象
     * @return 影响行数
     */
    int insert(Relationship relationship);
    /**
     * 通过主键删除数据
     *
     * @param toUserId 主键
     * @return 影响行数
     */
    int deleteById(Long toUserId);


    /**
     * 删除
     *
     * @return 影响行数
     */
    @Delete("delete from relationship where to_user_id = #{toUserId} AND from_user_id = #{fromUserId}")
    void delete(Relationship relationship);



    /**
     * 根据关注者id查找所有记录（查找关注的人的id）
     *
     * @param fromUserId
     * @return
     */
    @Select(value = "select to_user_id from relationship where from_user_id = #{fromUserId}")
    List<Long> findByFromUserId(Long fromUserId);

    /**
     * 根据被关注者查找所有记录（查找粉丝的id）
     *
     * @param toUserId
     * @return
     */
    @Select(value = "select from_user_id from relationship where to_user_id = #{toUserId}")
    List<Long> findByToUserId(Long toUserId);


    /**
     * 查询该用户的互相关注id
     *
     * @param userId
     * @return
     */
    @Select("SELECT DISTINCT t1.from_user_id FROM (SELECT * FROM relationship WHERE to_user_id = #{userId})  AS t1 INNER JOIN relationship t2 ON t1.from_user_id = t2.to_user_id")
    List<Long> findFriendsByUserId( Long userId);

    /**
     * 查询关注数
     *
     * @param fromUserId
     * @return
     */
    @Select("select count(*) from relationship where from_user_id = #{fromUserId}")
    Long countByFromUserId(Long fromUserId);

    /**
     * 查询粉丝数
     *
     * @param toUserId
     * @return
     */
    @Select("select count(*) from relationship where to_user_id = #{toUserId}")
    Long countByToUserId(Long toUserId);

    /**
     * 判断一个用户是否关注了另一个用户
     *
     * @param fromUserId
     * @param toUserId
     * @return
     */
    @Select("select * from relationship where from_user_id = #{fromUserId} AND to_user_id = #{toUserId} limit 1")
    Relationship findByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    /**
     * 查询一个用户是否被一个用户关注
     *
     * @param toUserId
     * @param fromUserId
     * @return
     */
    @Select("select * from relationship where to_user_id = #{toUserId} AND from_user_id = #{fromUserId} limit 1")
    Relationship findByToUserIdAndFromUserId(@Param("toUserId") Long toUserId, @Param("fromUserId") Long fromUserId);

}