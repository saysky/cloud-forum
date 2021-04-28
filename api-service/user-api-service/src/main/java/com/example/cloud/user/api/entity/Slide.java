package com.example.cloud.user.api.entity;

import java.io.Serializable;

/**
 * 首页幻灯片表(Slide)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:54
 */
public class Slide implements Serializable {
    private static final long serialVersionUID = -93869382766377758L;
    /**
    * ID
    */
    private Long id;
    /**
    * 跳转URL
    */
    private String guid;
    /**
    * 图片URL
    */
    private String picture;
    /**
    * 排序
    */
    private Integer position;
    /**
    * 状态
    */
    private String status;
    /**
    * 标题
    */
    private String title;


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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}