package com.example.cloud.qa.service;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.entity.Question;
import com.example.cloud.qa.api.feign.QuestionService;
import com.example.cloud.qa.dao.QuestionDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 提问表(Question)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@Service("questionService")
public class QuestionServiceImpl implements QuestionService {
    @Resource
    private QuestionDao questionDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Question queryById(Long id) {
        return this.questionDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Question> queryAllByLimit(int offset, int limit) {
        return this.questionDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param question 实例对象
     * @return 实例对象
     */
    @Override
    public Question insert(Question question) {
        this.questionDao.insert(question);
        return question;
    }

    /**
     * 修改数据
     *
     * @param question 实例对象
     * @return 实例对象
     */
    @Override
    public Question update(Question question) {
        this.questionDao.update(question);
        return this.queryById(question.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.questionDao.deleteById(id) > 0;
    }

    @Override
    public PageVO<Question> findAll(Question condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, condition.getOrderBy());
        List<Question> lists = questionDao.queryAll(condition);
        PageInfo<Question> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);
    }

    @Override
    public void viewIncrease(Long id) {
        questionDao.viewIncrease(id);
    }
}