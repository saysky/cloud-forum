package com.example.cloud.user.service;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.JobService;
import com.example.cloud.user.dao.JobDao;
import com.example.cloud.user.dao.UserDao;
import com.example.cloud.user.api.feign.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Resource
    private JobDao jobDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public User queryById(Long id) {
        User user = this.userDao.queryById(id);
        user.setJob(jobDao.queryById(user.getJobId()));
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return this.userDao.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return this.userDao.findByEmail(email);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<User> queryAllByLimit(int offset, int limit) {
        return this.userDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User insert(User user) {
        this.userDao.insert(user);
        return user;
    }

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User update(User user) {
        this.userDao.update(user);
        return this.queryById(user.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.userDao.deleteById(id) > 0;
    }

    @Override
    public PageVO<User> findAll(User condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> lists = userDao.queryAll(condition);
        PageInfo<User> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);

    }
}