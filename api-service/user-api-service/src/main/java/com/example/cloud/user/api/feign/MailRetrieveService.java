package com.example.cloud.user.api.feign;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.common.vo.ResultVO;
import com.example.cloud.user.api.entity.Job;
import com.example.cloud.user.api.entity.MailRetrieve;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 邮件记录表(MailRetrieve)表服务接口
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@FeignClient(value = "user-service")
@RestController
public interface MailRetrieveService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @PostMapping("/mailRetrieve/queryById")
    MailRetrieve queryById(@RequestParam Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @PostMapping("/mailRetrieve/queryAllByLimit")
    List<MailRetrieve> queryAllByLimit(@RequestParam int offset, @RequestParam int limit);

    /**
     * 新增数据
     *
     * @param mailRetrieve 实例对象
     * @return 实例对象
     */
    @PostMapping("/mailRetrieve/insert")
    MailRetrieve insert(@RequestBody MailRetrieve mailRetrieve);

    /**
     * 修改数据
     *
     * @param mailRetrieve 实例对象
     * @return 实例对象
     */
    @PostMapping("/mailRetrieve/update")
    MailRetrieve update(@ModelAttribute MailRetrieve mailRetrieve);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @PostMapping("/mailRetrieve/deleteById")
    boolean deleteById(@RequestParam Long id);

    /**
     * 根据账号查找记录
     *
     * @param account
     * @return
     */
    @PostMapping("/mailRetrieve/findByAccount")
    MailRetrieve findByAccount(@RequestParam String account);


    /**
     * 生成验证邮件的URL
     *
     * @param basePath
     * @param account
     * @return
     */
    @PostMapping("/mailRetrieve/getEmailUrl")
    String getEmailUrl(@RequestParam("basePath") String basePath, @RequestParam("account") String account);

    /**
     * 邮件找回密码URL校验
     *
     * @param sid
     * @param account
     * @return
     */
    @PostMapping("/mailRetrieve/verifyMailUrl")
    ResultVO verifyMailUrl(@RequestParam("sid") String sid,
                           @RequestParam("account") String account);

    @PostMapping("/mailRetrieve/findAll")
    PageVO<MailRetrieve> findAll(@RequestBody MailRetrieve condition, @RequestParam int pageNum, @RequestParam int pageSize);


}