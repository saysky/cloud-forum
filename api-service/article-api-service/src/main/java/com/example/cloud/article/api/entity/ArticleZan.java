package com.example.cloud.article.api.entity;

import java.io.Serializable;

/**
 * 文章和赞关联表(ArticleZan)实体类
 *
 * @author makejava
 * @since 2021-03-30 14:44:35
 */
public class ArticleZan implements Serializable {
    private static final long serialVersionUID = -15372948256223327L;
    /**
    * 文章ID
    */
    private Long articleId;
    /**
    * 赞ID
    */
    private Long userId;
    /**
    * 主键
    */
    private Long id;


    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
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