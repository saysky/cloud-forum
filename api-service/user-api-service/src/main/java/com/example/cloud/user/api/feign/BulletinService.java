package com.example.cloud.user.api.feign;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.Bulletin;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告表(Bulletin)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@FeignClient(value = "user-service")
@RestController
public interface BulletinService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/bulletin/queryById")
    Bulletin queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @PostMapping("/bulletin/queryAllByLimit")
    List<Bulletin> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param bulletin 实例对象
     * @return 实例对象
     */
    @PostMapping("/bulletin/insert")
    Bulletin insert(@RequestBody Bulletin bulletin);

    /**
     * 修改数据
     *
     * @param bulletin 实例对象
     * @return 实例对象
     */
    @PostMapping("/bulletin/update")
    Bulletin update(@RequestBody Bulletin bulletin);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/bulletin/deleteById")
    boolean deleteById(@RequestParam Long id);


    @PostMapping("/bulletin/findAll")
    PageVO<Bulletin> findAll(@RequestBody Bulletin condition, @RequestParam int pageNum, @RequestParam int pageSize);

    @PostMapping("/bulletin/findByName")
    Bulletin findByName(@RequestParam String name);


}