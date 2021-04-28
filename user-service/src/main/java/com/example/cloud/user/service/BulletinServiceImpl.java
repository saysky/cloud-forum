package com.example.cloud.user.service;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.Bulletin;
import com.example.cloud.user.api.feign.BulletinService;
import com.example.cloud.user.dao.BulletinDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告表(Bulletin)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@Service("bulletinService")
public class BulletinServiceImpl implements BulletinService {
    @Resource
    private BulletinDao bulletinDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Bulletin queryById(Long id) {
        return this.bulletinDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<Bulletin> queryAllByLimit(int offset, int limit) {
        return this.bulletinDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param bulletin 实例对象
     * @return 实例对象
     */
    @Override
    public Bulletin insert(Bulletin bulletin) {
        this.bulletinDao.insert(bulletin);
        return bulletin;
    }

    /**
     * 修改数据
     *
     * @param bulletin 实例对象
     * @return 实例对象
     */
    @Override
    public Bulletin update(Bulletin bulletin) {
        this.bulletinDao.update(bulletin);
        return this.queryById(bulletin.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.bulletinDao.deleteById(id) > 0;
    }

    @Override
    public PageVO<Bulletin> findAll(Bulletin condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Bulletin> lists = bulletinDao.queryAll(condition);
        PageInfo<Bulletin> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);
    }

    @Override
    public Bulletin findByName(String name) {
        return bulletinDao.findByName(name);
    }
}