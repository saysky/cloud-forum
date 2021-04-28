package com.example.cloud.qa.service;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.entity.Answer;
import com.example.cloud.qa.api.entity.AnswerCai;
import com.example.cloud.qa.api.entity.AnswerZan;
import com.example.cloud.qa.api.feign.AnswerService;
import com.example.cloud.qa.dao.AnswerCaiDao;
import com.example.cloud.qa.dao.AnswerDao;
import com.example.cloud.qa.dao.AnswerZanDao;
import com.example.cloud.user.api.feign.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.stream.events.Comment;
import java.util.List;

/**
 * 回答表(Answer)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@Service("answerService")
public class AnswerServiceImpl implements AnswerService {
    @Resource
    private AnswerDao answerDao;
    @Resource
    private AnswerZanDao answerZanDao;
    @Resource
    private AnswerCaiDao answerCaiDao;
    @Resource
    private UserService userService;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Answer queryById(Long id) {
        return this.answerDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Answer> queryAllByLimit(int offset, int limit) {
        return this.answerDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param answer 实例对象
     * @return 实例对象
     */
    @Override
    public Answer insert(Answer answer) {
        this.answerDao.insert(answer);
        return answer;
    }

    /**
     * 修改数据
     *
     * @param answer 实例对象
     * @return 实例对象
     */
    @Override
    public Answer update(Answer answer) {
        this.answerDao.update(answer);
        return this.queryById(answer.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.answerDao.deleteById(id) > 0;
    }

    @Override
    public int createZan(Long answerId, Long userId) {
        AnswerZan answerZan = answerZanDao.queryByUserIdAndAnswerId(userId, answerId);
        if (answerZan == null) {
            // 点赞
            answerZan = new AnswerZan();
            answerZan.setAnswerId(answerId);
            answerZan.setUserId(userId);
            answerZanDao.insert(answerZan);
            Answer answer = answerDao.queryById(answerId);
            if (answer != null) {
                answer.setZanSize(answer.getZanSize() + 1);
                answerDao.update(answer);
            }
            return answer.getZanSize();
        } else {
            // 取消点赞
            answerZanDao.deleteById(answerZan.getId());
            Answer answer = answerDao.queryById(answerId);
            if (answer != null) {
                answer.setZanSize(answer.getZanSize() - 1 > 0 ? answer.getZanSize() - 1 : 0);
                answerDao.update(answer);
            }
            return answer.getZanSize();
        }
    }

    @Override
    public int createCai(Long answerId, Long userId) {
        AnswerCai answerCai = answerCaiDao.queryByUserIdAndAnswerId(userId, answerId);
        if (answerCai == null) {
            // 点赞
            answerCai = new AnswerCai();
            answerCai.setAnswerId(answerId);
            answerCai.setUserId(userId);
            answerCaiDao.insert(answerCai);
            Answer answer = answerDao.queryById(answerId);
            if (answer != null) {
                answer.setCaiSize(answer.getCaiSize() + 1);
                answerDao.update(answer);
            }
            return answer.getCaiSize();
        } else {
            // 取消点赞
            answerCaiDao.deleteById(answerCai.getId());
            Answer answer = answerDao.queryById(answerId);
            if (answer != null) {
                answer.setCaiSize(answer.getCaiSize() - 1 > 0 ? answer.getCaiSize() - 1 : 0);
                answerDao.update(answer);
            }
            return answer.getCaiSize();
        }
    }

    @Override
    public PageVO<Answer> findAll(Answer condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, condition.getOrderBy());
        List<Answer> lists = answerDao.queryAll(condition);
        PageInfo<Answer> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);
    }


    @Override
    public List<Answer> findByPid(Long pid) {
        Answer condition = new Answer();
        condition.setPid(pid);
        List<Answer> commentList = answerDao.queryAll(condition);
        for (Answer temp : commentList) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        return commentList;
    }
}