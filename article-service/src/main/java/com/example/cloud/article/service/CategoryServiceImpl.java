package com.example.cloud.article.service;

import com.example.cloud.article.api.entity.Category;
import com.example.cloud.article.api.entity.Category;
import com.example.cloud.article.dao.CategoryDao;
import com.example.cloud.article.api.feign.CategoryService;
import com.example.cloud.common.vo.PageVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * (Category)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryDao categoryDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Category queryById(Long id) {
        return this.categoryDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Category> queryAllByLimit(int offset, int limit) {
        return this.categoryDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param category 实例对象
     * @return 实例对象
     */
    @Override
    public Category insert(Category category) {
        this.categoryDao.insert(category);
        return category;
    }

    /**
     * 修改数据
     *
     * @param category 实例对象
     * @return 实例对象
     */
    @Override
    public Category update(Category category) {
        this.categoryDao.update(category);
        return this.queryById(category.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.categoryDao.deleteById(id) > 0;
    }

    @Override
    public PageVO<Category> findAll(Category condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Category> lists = categoryDao.queryAll(condition);
        PageInfo<Category> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);

    }

    @Override
    public List<Category> findByUserIdAndName(Long userId, String name) {
        return this.categoryDao.findByUserIdAndName(userId, name);
    }

    @Override
    public List<Category> findByUserId( Long userId) {
        return categoryDao.findByUserId(userId);
    }

    @Override
    public void changePriority(Long userId, Long currentId, Long otherId) {
        Category currentCategory = categoryDao.queryById(currentId);//当前分类
        Category otherCategory = categoryDao.queryById(otherId);//另一个分类
        if(currentCategory == null || otherCategory == null) {
            return;
        }
        if(!Objects.equals(currentCategory.getUserId(), userId) ||
                !Objects.equals(otherCategory.getUserId(), userId)) {
            return;
        }
        Integer currentPriority = currentCategory.getPosition();//1
        Integer otherPriority = otherCategory.getPosition();//2
        //交换
        currentCategory.setPosition(otherPriority);
        otherCategory.setPosition(currentPriority);
        //写入数据库
        categoryDao.update(currentCategory);
        categoryDao.update(otherCategory);
    }
}