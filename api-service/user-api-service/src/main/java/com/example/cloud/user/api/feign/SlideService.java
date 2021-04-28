package com.example.cloud.user.api.feign;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.Relationship;
import com.example.cloud.user.api.entity.Slide;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页幻灯片表(Slide)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
@FeignClient(value = "user-service")
@RestController
public interface SlideService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/slide/queryById")
    Slide queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @PostMapping("/slide/queryAllByLimit")
    List<Slide> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param slide 实例对象
     * @return 实例对象
     */
    @PostMapping("/slide/insert")
    Slide insert(@RequestBody Slide slide);

    /**
     * 修改数据
     *
     * @param slide 实例对象
     * @return 实例对象
     */
    @PostMapping("/slide/update")
    Slide update(@RequestBody Slide slide);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/slide/deleteById")
    boolean deleteById(@RequestParam Long id);


    @PostMapping("/slide/findAll")
    PageVO<Slide> findAll(@RequestBody Slide condition, @RequestParam int pageNum, @RequestParam int pageSize);

}