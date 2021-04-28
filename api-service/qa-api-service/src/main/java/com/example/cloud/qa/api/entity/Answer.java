package com.example.cloud.qa.api.entity;

import com.example.cloud.common.util.DateUtil;
import com.example.cloud.user.api.entity.User;

import java.util.Date;
import java.io.Serializable;
import java.util.List;

/**
 * 回答表(Answer)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public class Answer implements Serializable {
    private static final long serialVersionUID = -70235610237267763L;
    /**
     * ID
     */
    private Long id;
    /**
     * 踩数量
     */
    private Integer caiSize;
    /**
     * 评论数量
     */
    private Integer commentSize;
    /**
     * 内容
     */
    private String content;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 是否采纳
     */
    private Integer isAccepted;
    /**
     * 父回复ID
     */
    private Long pid;
    /**
     * 状态
     */
    private String status;
    /**
     * 赞数量
     */
    private Integer zanSize;
    /**
     * 问题ID
     */
    private Long questionId;
    /**
     * 回复者用户ID
     */
    private Long replyUserId;
    /**
     * 回答者用户ID
     */
    private Long userId;


    public String easyCreateTime;

    private User user;

    private String orderBy = "id DESC";

    private List<Answer> answerList;

    private Question question;

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCaiSize() {
        return caiSize;
    }

    public void setCaiSize(Integer caiSize) {
        this.caiSize = caiSize;
    }

    public Integer getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(Integer commentSize) {
        this.commentSize = commentSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Integer isAccepted) {
        this.isAccepted = isAccepted;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getZanSize() {
        return zanSize;
    }

    public void setZanSize(Integer zanSize) {
        this.zanSize = zanSize;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }


    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}