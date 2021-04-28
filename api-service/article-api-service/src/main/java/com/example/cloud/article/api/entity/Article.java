package com.example.cloud.article.api.entity;

import com.example.cloud.common.util.DateUtil;
import com.example.cloud.user.api.entity.User;

import java.beans.Transient;
import java.util.Arrays;
import java.util.Date;
import java.io.Serializable;
import java.util.List;

/**
 * 文章表(Article)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public class Article implements Serializable {
    private static final long serialVersionUID = -86202292570656347L;
    /**
    * 文章ID
    */
    private Long id;
    /**
    * 收藏数
    */
    private Integer bookmarkSize;
    /**
    * 评论数
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
    * URL
    */
    private String guid;
    /**
    * 是否允许评论
    */
    private Integer isAllowComment;
    /**
    * 是否置顶
    */
    private Integer isSticky;
    /**
    * 状态
    */
    private String status;
    /**
    * 摘要
    */
    private String summary;
    /**
    * 标签数
    */
    private String tags;
    /**
    * 标题
    */
    private String title;
    /**
    * 更新时间
    */
    private Date updateTime;
    /**
    * 访问数
    */
    private Integer viewSize;
    /**
    * 点赞数
    */
    private Integer zanSize;
    /**
    * 分类ID
    */
    private Long categoryId;
    /**
    * 用户ID
    */
    private Long userId;

    /**
     * 非数据库字段，用于排序
     */
    private String orderBy = "id DESC";

    private User user;

    private Category category;

    public String easyCreateTime;

    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }

    private List<String> tagList;

    public List<String> getTagList() {
        if (tags != null) {
            String[] arr = tags.split(",|，",5);
            tagList = Arrays.asList(arr);
            return tagList;
        } else {
            return null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBookmarkSize() {
        return bookmarkSize;
    }

    public void setBookmarkSize(Integer bookmarkSize) {
        this.bookmarkSize = bookmarkSize;
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getIsAllowComment() {
        return isAllowComment;
    }

    public void setIsAllowComment(Integer isAllowComment) {
        this.isAllowComment = isAllowComment;
    }

    public Integer getIsSticky() {
        return isSticky;
    }

    public void setIsSticky(Integer isSticky) {
        this.isSticky = isSticky;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getViewSize() {
        return viewSize;
    }

    public void setViewSize(Integer viewSize) {
        this.viewSize = viewSize;
    }

    public Integer getZanSize() {
        return zanSize;
    }

    public void setZanSize(Integer zanSize) {
        this.zanSize = zanSize;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}