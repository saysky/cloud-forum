package com.example.cloud.user.service;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.Job;
import com.example.cloud.user.dao.JobDao;
import com.example.cloud.user.api.feign.JobService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 职业表(Job)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@Service("jobService")
public class JobServiceImpl implements JobService {
    @Resource
    private JobDao jobDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Job queryById(Long id) {
        return this.jobDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<Job> queryAllByLimit(int offset, int limit) {
        return this.jobDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param job 实例对象
     * @return 实例对象
     */
    @Override
    public Job insert(Job job) {
        this.jobDao.insert(job);
        return job;
    }

    /**
     * 修改数据
     *
     * @param job 实例对象
     * @return 实例对象
     */
    @Override
    public Job update(Job job) {
        this.jobDao.update(job);
        return this.queryById(job.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.jobDao.deleteById(id) > 0;
    }

    @Override
    public PageVO<Job> findAll(Job condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Job> lists = jobDao.queryAll(condition);
        PageInfo<Job> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);

    }
}