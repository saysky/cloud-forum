package com.example.cloud.user.api.feign;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
@FeignClient(value = "user-service")
@RestController
public interface UserService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/user/queryById")
    User queryById(@RequestParam Long id);


    @PostMapping("/user/findByUsername")
    User findByUsername(@RequestParam String username);


    @PostMapping("/user/findByEmail")
    User findByEmail(@RequestParam String email);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @PostMapping("/user/queryAllByLimit")
    List<User> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @PostMapping("/user/insert")
    User insert(@RequestBody User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @PostMapping("/user/update")
    User update(@RequestBody User user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/user/deleteById")
    boolean deleteById(@RequestParam Long id);


    @PostMapping("/user/findAll")
    PageVO<User> findAll(@RequestBody User condition, @RequestParam int pageNum, @RequestParam int pageSize);

}