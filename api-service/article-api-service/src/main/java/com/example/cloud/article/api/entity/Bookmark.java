package com.example.cloud.article.api.entity;

import com.example.cloud.common.util.DateUtil;

import java.util.Date;
import java.io.Serializable;

/**
 * 收藏表(Bookmark)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public class Bookmark implements Serializable {
    private static final long serialVersionUID = 738286259726399207L;
    /**
    * ID
    */
    private Long id;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 文章ID
    */
    private Long articleId;
    /**
    * 用户ID
    */
    private Long userId;


    public String easyCreateTime;

    private Article article;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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

    public Bookmark() {
    }

    public Bookmark(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}