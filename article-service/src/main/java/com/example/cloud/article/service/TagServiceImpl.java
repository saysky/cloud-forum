package com.example.cloud.article.service;

import com.example.cloud.article.api.entity.Tag;
import com.example.cloud.article.api.entity.Tag;
import com.example.cloud.article.dao.TagDao;
import com.example.cloud.article.api.feign.TagService;
import com.example.cloud.common.vo.PageVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文章标签表(Tag)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
@Service("tagService")
public class TagServiceImpl implements TagService {
    @Resource
    private TagDao tagDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Tag queryById(Long id) {
        return this.tagDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Tag> queryAllByLimit(int offset, int limit) {
        return this.tagDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param tag 实例对象
     * @return 实例对象
     */
    @Override
    public Tag insert(Tag tag) {
        this.tagDao.insert(tag);
        return tag;
    }

    /**
     * 修改数据
     *
     * @param tag 实例对象
     * @return 实例对象
     */
    @Override
    public Tag update(Tag tag) {
        this.tagDao.update(tag);
        return this.queryById(tag.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.tagDao.deleteById(id) > 0;
    }

    @Override
    public PageVO<Tag> findAll(Tag condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Tag> lists = tagDao.queryAll(condition);
        PageInfo<Tag> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);

    }

    @Override
    public Tag findByName(String name) {
        return tagDao.findByName(name);
    }
}