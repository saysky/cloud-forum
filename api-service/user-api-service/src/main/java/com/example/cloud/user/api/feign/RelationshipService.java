package com.example.cloud.user.api.feign;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.Relationship;
import com.example.cloud.user.api.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 粉丝关系表(Relationship)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
@FeignClient(value = "user-service")
@RestController
public interface RelationshipService {

    /**
     * 通过ID查询单条数据
     *
     * @param toUserId 主键
     * @return 实例对象
     */
    @PostMapping("/relationship/queryById")
    Relationship queryById(@RequestParam Long toUserId);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @PostMapping("/relationship/queryAllByLimit")
    List<Relationship> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param relationship 实例对象
     * @return 实例对象
     */
    @PostMapping("/relationship/insert")
    Relationship insert(@RequestBody Relationship relationship);


    /**
     * 通过主键删除数据
     *
     * @param toUserId 主键
     * @return 是否成功
     */
    @PostMapping("/relationship/deleteById")
    boolean deleteById(@RequestParam Long toUserId);

    @PostMapping("/relationship/findAll")
    PageVO<Relationship> findAll(@RequestBody Relationship condition, @RequestParam int pageNum, @RequestParam int pageSize);

    /**
     * 列出所有的关注者
     *
     * @return
     */
    @PostMapping("/relationship/listFollows")
    PageVO<User> listFollows(@RequestParam Long userId, @RequestParam int pageNum, @RequestParam int pageSize);


    /**
     * 列出所有的粉丝
     *
     * @return
     */
    @PostMapping("/relationship/listFans")
    PageVO<User> listFans(@RequestParam Long userId, @RequestParam int pageNum, @RequestParam int pageSize);

    /**
     * 列出互相关注的id
     *
     * @param userId
     * @return
     */
    @PostMapping("/relationship/listFriends")
    List<Long> listFriends(@RequestParam Long userId);

    /**
     * 添加关系
     *
     * @param relationship
     */
    @PostMapping("/relationship/saveRelationship")
    void saveRelationship(@RequestBody Relationship relationship);

    /**
     * 去除关系
     *
     * @param relationship
     */
    @PostMapping("/relationship/removeRelationship")
    void removeRelationship(@RequestBody Relationship relationship);

    
    /**
     * 更新关注数
     */
    @PostMapping("/relationship/updateFollowSize")
    void updateFollowSize(@RequestParam Long userId);

    /**
     * 更新粉丝数
     */
    @PostMapping("/relationship/updateFanSize")
    void updateFanSize(@RequestParam Long userId);

    /**
     * 获得两个用户之间的关系，0未关注，1已关注，2互相关注
     * @param fromUserId
     * @param toUserId
     * @return
     */
    @PostMapping("/relationship/getRelationshipBetweenUsers")
    Integer getRelationshipBetweenUsers(@RequestParam Long fromUserId, @RequestParam Long toUserId);

}