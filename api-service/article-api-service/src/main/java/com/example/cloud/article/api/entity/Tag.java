package com.example.cloud.article.api.entity;

import java.io.Serializable;

/**
 * 文章标签表(Tag)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = -51100220598917531L;
    /**
    * ID
    */
    private Long id;
    /**
    * 文章数
    */
    private Integer articleSize;
    /**
    * URL
    */
    private String guid;
    /**
    * 名称
    */
    private String name;
    /**
    * 排序
    */
    private Integer position;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getArticleSize() {
        return articleSize;
    }

    public void setArticleSize(Integer articleSize) {
        this.articleSize = articleSize;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

}