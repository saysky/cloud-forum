package com.example.cloud.user.api.feign;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.Job;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 职业表(Job)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@FeignClient(value = "user-service")
@RestController
public interface JobService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/job/queryById")
    Job queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @PostMapping("/job/queryAllByLimit")
    List<Job> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param job 实例对象
     * @return 实例对象
     */
    @PostMapping("/job/insert")
    Job insert(@RequestBody Job job);

    /**
     * 修改数据
     *
     * @param job 实例对象
     * @return 实例对象
     */
    @PostMapping("/job/update")
    Job update(Job job);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/job/deleteById")
    boolean deleteById(@RequestParam Long id);


    @PostMapping("/job/findAll")
    PageVO<Job> findAll(@RequestBody Job condition, @RequestParam int pageNum, @RequestParam int pageSize);

}