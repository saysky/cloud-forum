package com.example.cloud.qa.api.feign;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.entity.Answer;
import com.example.cloud.qa.api.entity.Question;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回答表(Answer)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@FeignClient(value = "qa-service")
@RestController
public interface AnswerService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/answer/queryById")
    Answer queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @PostMapping("/answer/queryAllByLimit")
    List<Answer> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param answer 实例对象
     * @return 实例对象
     */
    @PostMapping("/answer/insert")
    Answer insert(@RequestBody Answer answer);

    /**
     * 修改数据
     *
     * @param answer 实例对象
     * @return 实例对象
     */
    @PostMapping("/answer/update")
    Answer update(@RequestBody Answer answer);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/answer/deleteById")
    boolean deleteById(@RequestParam Long id);

    /**
     * 点赞
     *
     * @param answerId
     * @return
     */
    @PostMapping("/answer/createZan")
    int createZan(@RequestParam Long answerId, @RequestParam Long userId);

    /**
     * 点踩
     *
     * @param answerId
     * @return
     */
    @PostMapping("/answer/createCai")
    int createCai(@RequestParam Long answerId, @RequestParam Long userId);


    @PostMapping("/answer/findAll")
    PageVO<Answer> findAll(@RequestBody Answer condition, @RequestParam int pageNum, @RequestParam int pageSize);

    @PostMapping("/answer/findByPid")
    List<Answer> findByPid(@RequestParam Long pid);
}