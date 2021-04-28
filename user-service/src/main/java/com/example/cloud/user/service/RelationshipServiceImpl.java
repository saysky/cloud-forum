package com.example.cloud.user.service;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.entity.Relationship;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.dao.RelationshipDao;
import com.example.cloud.user.api.feign.RelationshipService;
import com.example.cloud.user.dao.UserDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 粉丝关系表(Relationship)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
@Service("relationshipService")
public class RelationshipServiceImpl implements RelationshipService {
    @Resource
    private RelationshipDao relationshipDao;

    @Resource
    private UserDao userDao;


    /**
     * 通过ID查询单条数据
     *
     * @param toUserId 主键
     * @return 实例对象
     */
    @Override
    public Relationship queryById(Long toUserId) {
        return this.relationshipDao.queryById(toUserId);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Relationship> queryAllByLimit(int offset, int limit) {
        return this.relationshipDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param relationship 实例对象
     * @return 实例对象
     */
    @Override
    public Relationship insert(Relationship relationship) {
        this.relationshipDao.insert(relationship);
        return relationship;
    }


    /**
     * 通过主键删除数据
     *
     * @param toUserId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long toUserId) {
        return this.relationshipDao.deleteById(toUserId) > 0;
    }

    @Override
    public PageVO<Relationship> findAll(Relationship condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Relationship> lists = relationshipDao.queryAll(condition);
        PageInfo<Relationship> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);

    }

    @Override
    public PageVO<User> listFollows(Long userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Relationship condition = new Relationship();
        condition.setFromUserId(userId);
        List<Relationship> lists = relationshipDao.queryAll(condition);
        List<User> userList = new ArrayList<>();
        for (Relationship item : lists) {
            userList.add(userDao.queryById(item.getToUserId()));
        }
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return PageVO.build(pageInfo);
    }

    @Override
    public PageVO<User> listFans(Long userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Relationship condition = new Relationship();
        condition.setToUserId(userId);
        List<Relationship> lists = relationshipDao.queryAll(condition);
        List<User> userList = new ArrayList<>();
        for (Relationship item : lists) {
            userList.add(userDao.queryById(item.getFromUserId()));
        }
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return PageVO.build(pageInfo);
    }

    @Override
    public List<Long> listFriends(Long userId) {
        List<Long> relationshipList = relationshipDao.findFriendsByUserId(userId);
        return relationshipList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveRelationship(Relationship relationship) {
        //删除关系
        relationshipDao.delete(relationship);
        //1、添加关注
        relationshipDao.insert(relationship);
        //2、更新双方关注数和粉丝数
        updateFollowSize(relationship.getFromUserId());
        updateFanSize(relationship.getToUserId());

    }

    @Override
    public void removeRelationship(Relationship relationship) {
        //删除关系
        relationshipDao.delete(relationship);
        //更新双方关注数和粉丝数
        updateFollowSize(relationship.getFromUserId());
        updateFanSize(relationship.getToUserId());
    }

    @Override
    public void updateFollowSize(Long userId) {
        User user = userDao.queryById(userId);
        user.setFollowSize(relationshipDao.countByFromUserId(userId));
        userDao.update(user);
    }

    @Override
    public void updateFanSize(Long userId) {
        User user = userDao.queryById(userId);
        if (user != null) {
            user.setFanSize(relationshipDao.countByToUserId(userId));
            userDao.update(user);
        }

    }

    @Override
    public Integer getRelationshipBetweenUsers(Long fromUserId, Long toUserId) {
        if (relationshipDao.findByFromUserIdAndToUserId(fromUserId, toUserId) != null) {
            //已关注
            if (relationshipDao.findByToUserIdAndFromUserId(fromUserId, toUserId) == null) {
                return 1;
            } else {
                //互相关注
                return 2;
            }
        } else {
            //未关注
            return 0;
        }
    }

}