package com.example.cloud.qa.api.entity;

import com.example.cloud.common.util.DateUtil;
import com.example.cloud.user.api.entity.User;

import java.util.Arrays;
import java.util.Date;
import java.io.Serializable;
import java.util.List;

/**
 * 提问表(Question)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public class Question implements Serializable {
    private static final long serialVersionUID = -22870170536769808L;
    /**
    * ID
    */
    private Long id;
    /**
    * 回答数量
    */
    private Integer answerSize;
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
    * 状态
    */
    private String status;
    /**
    * 摘要
    */
    private String summary;
    /**
    * 标签
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
    * 用户ID
    */
    private Long userId;

    /**
     * 排序
     */
    private String orderBy = "id DESC";

    private User user;



    public String easyCreateTime;

    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }

    public String easyViewSize;

    public String getEasyViewSize() {
        Integer views = getViewSize();
        if (views == null) {
            return "1";
        }
        if (views < 1000) {
            return views.toString();
        }
        Integer suffix = views % 1000 / 100;
        if (suffix == 0) {
            return views / 1000 + "k";
        } else {
            return views / 1000 + "." + views % 1000 / 100 + "k";
        }
    }

    public List<String> tagList;

    public List<String> getTagList() {
        if (tags != null) {
            String[] arr = tags.split(",|，", 5);
            List<String> list = Arrays.asList(arr);
            return list;
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

    public Integer getAnswerSize() {
        return answerSize;
    }

    public void setAnswerSize(Integer answerSize) {
        this.answerSize = answerSize;
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
}