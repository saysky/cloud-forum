package com.example.cloud.article.service;

import com.example.cloud.article.api.entity.*;
import com.example.cloud.article.api.entity.Comment;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.dao.CommentCaiDao;
import com.example.cloud.article.dao.CommentDao;
import com.example.cloud.article.api.feign.CommentService;
import com.example.cloud.article.dao.CommentZanDao;
import com.example.cloud.common.enums.CommentStickStatusEnum;
import com.example.cloud.common.enums.PostStatusEnum;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.feign.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 文章评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@Service("commentService")
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentDao commentDao;
    @Resource
    private CommentZanDao commentZanDao;
    @Resource
    private CommentCaiDao commentCaiDao;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Comment queryById(Long id) {
        return this.commentDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Comment> queryAllByLimit(int offset, int limit) {
        return this.commentDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public Comment insert(Comment comment) {
        comment.setCreateTime(new Date());
        comment.setCaiSize(0);
        comment.setZanSize(0);
        comment.setIsSticky(CommentStickStatusEnum.NOT_STICKY_STATUS.getCode());
        comment.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
        if (comment.getPid() == null) {
            comment.setPid(0L);
        }
        Integer max = commentDao.getMaxCommentFloor(comment.getArticleId());
        comment.setFloor((max != null ? max : 0) + 1);
        this.commentDao.insert(comment);
        return comment;
    }

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public Comment update(Comment comment) {
        this.commentDao.update(comment);
        return this.queryById(comment.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.commentDao.deleteById(id) > 0;
    }

    @Override
    public int createZan(Long commentId, Long userId) {
        CommentZan commentZan = commentZanDao.queryByUserIdAndCommentId(userId, commentId);
        if (commentZan == null) {
            // 点赞
            commentZan = new CommentZan();
            commentZan.setCommentId(commentId);
            commentZan.setUserId(userId);
            commentZanDao.insert(commentZan);
            Comment comment = commentDao.queryById(commentId);
            if (comment != null) {
                comment.setZanSize(comment.getZanSize() + 1);
                commentDao.update(comment);
            }
            return comment.getZanSize();
        } else {
            // 取消点赞
            commentZanDao.deleteById(commentZan.getId());
            Comment comment = commentDao.queryById(commentId);
            if (comment != null) {
                comment.setZanSize(comment.getZanSize() - 1 > 0 ? comment.getZanSize() - 1 : 0);
                commentDao.update(comment);
            }
            return comment.getZanSize();
        }
    }

    @Override
    public int createCai(Long commentId, Long userId) {
        CommentCai commentCai = commentCaiDao.queryByUserIdAndCommentId(userId, commentId);
        if (commentCai == null) {
            // 点赞
            commentCai = new CommentCai();
            commentCai.setCommentId(commentId);
            commentCai.setUserId(userId);
            commentCaiDao.insert(commentCai);
            Comment comment = commentDao.queryById(commentId);
            if (comment != null) {
                comment.setCaiSize(comment.getCaiSize() + 1);
                commentDao.update(comment);
            }
            return comment.getCaiSize();

        } else {
            // 取消点赞
            commentCaiDao.deleteById(commentCai.getId());
            Comment comment = commentDao.queryById(commentId);
            if (comment != null) {
                comment.setCaiSize(comment.getCaiSize() - 1 > 0 ? comment.getCaiSize() - 1 : 0);
                commentDao.update(comment);
            }
            return comment.getCaiSize();

        }
    }

    @Override
    public PageVO<Comment> findAll(Comment condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, condition.getOrderBy());
        List<Comment> lists = commentDao.queryAll(condition);
        PageInfo<Comment> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);
    }

    @Override
    public Integer countCommentSizeByArticle(Long articleId) {
        return commentDao.countCommentSizeByArticle(articleId);
    }

    @Override
    public Integer countCommentByStatus(String status) {
        return commentDao.countCommentByStatus(status);
    }

    @Override
    public List<Comment> findByPid(Long pid) {
        List<Comment> commentList = commentDao.findByPid(pid);
        for (Comment temp : commentList) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        return commentList;
    }

    @Override
    public void replyComment(Long userId, Long articleId, Long commentId, Long replyId, String commentContent) {
        Comment comment = new Comment(userId, articleId, commentId, commentContent);
        //评论回复，需要加上@用户
        Comment replyComment = null;
        if (replyId != null) {
            replyComment = commentDao.queryById(replyId);
            comment.setReplyUserId(replyComment.getUserId());
        } else {
            replyComment = commentDao.queryById(commentId);
            comment.setReplyUserId(replyComment.getUserId());
        }
        //添加评论
        this.insert(comment);
    }


}