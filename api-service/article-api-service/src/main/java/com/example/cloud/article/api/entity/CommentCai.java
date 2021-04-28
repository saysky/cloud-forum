package com.example.cloud.article.api.entity;

import java.io.Serializable;

/**
 * 评论和踩关联表(CommentCai)实体类
 *
 * @author makejava
 * @since 2021-03-30 14:44:35
 */
public class CommentCai implements Serializable {
    private static final long serialVersionUID = 643832315470319342L;
    /**
    * 评论ID
    */
    private Long commentId;
    /**
    * 用户ID
    */
    private Long userId;
    /**
    * 主键
    */
    private Long id;


    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}