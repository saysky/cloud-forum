package com.example.cloud.user.service;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.Slide;
import com.example.cloud.user.dao.SlideDao;
import com.example.cloud.user.api.feign.SlideService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页幻灯片表(Slide)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
@Service("slideService")
public class SlideServiceImpl implements SlideService {
    @Resource
    private SlideDao slideDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Slide queryById(Long id) {
        return this.slideDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<Slide> queryAllByLimit(int offset, int limit) {
        return this.slideDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param slide 实例对象
     * @return 实例对象
     */
    @Override
    public Slide insert(Slide slide) {
        this.slideDao.insert(slide);
        return slide;
    }

    /**
     * 修改数据
     *
     * @param slide 实例对象
     * @return 实例对象
     */
    @Override
    public Slide update(Slide slide) {
        this.slideDao.update(slide);
        return this.queryById(slide.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.slideDao.deleteById(id) > 0;
    }

    @Override
    public PageVO<Slide> findAll(Slide condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Slide> lists = slideDao.queryAll(condition);
        PageInfo<Slide> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);

    }
}