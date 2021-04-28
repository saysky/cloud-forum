package com.example.cloud.qa.api.feign;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.entity.Question;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提问表(Question)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@FeignClient(value = "qa-service")
@RestController
public interface QuestionService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/question/queryById")
    Question queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @PostMapping("/question/queryAllByLimit")
    List<Question> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param question 实例对象
     * @return 实例对象
     */
    @PostMapping("/question/insert")
    Question insert(@RequestBody Question question);

    /**
     * 修改数据
     *
     * @param question 实例对象
     * @return 实例对象
     */
    @PostMapping("/question/update")
    Question update(@RequestBody Question question);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/question/deleteById")
    boolean deleteById(@RequestParam Long id);

    @PostMapping("/question/findAll")
    PageVO<Question> findAll(@RequestBody Question condition, @RequestParam int pageNum, @RequestParam int pageSize);

    /**
     * 访问量加1
     *
     * @param id
     * @return
     */
    @PostMapping("/question/viewIncrease")
    void viewIncrease(@RequestParam Long id);
}