package com.example.cloud.article.api.entity;

import com.example.cloud.common.util.DateUtil;

import java.io.Serializable;

/**
 * (Category)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public class Category implements Serializable {
    private static final long serialVersionUID = -82695121991046713L;
    /**
    * 分类ID
    */
    private Long id;
    /**
    * URL
    */
    private String guid;
    /**
    * 是否在前台显示
    */
    private String isHidden;
    /**
    * 名称
    */
    private String name;
    /**
    * 排序
    */
    private Integer position;
    /**
    * 用户ID
    */
    private Long userId;

    public String easyCreateTime;

    private Long articleSize;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleSize() {
        return articleSize;
    }

    public void setArticleSize(Long articleSize) {
        this.articleSize = articleSize;
    }
}