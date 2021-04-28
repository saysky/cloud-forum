package com.example.cloud.user.api.entity;

import com.example.cloud.common.util.DateUtil;

import java.util.Date;
import java.io.Serializable;

/**
 * 公告表(Bulletin)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public class Bulletin implements Serializable {
    private static final long serialVersionUID = -48785121171899727L;
    /**
    * ID
    */
    private Long id;
    /**
    * 内容
    */
    private Object content;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * URL
    */
    private String guid;
    /**
    * 是否置顶
    */
    private Integer isSticky;
    /**
    * 名称
    */
    private String name;
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
    /**
    * 用户ID
    */
    private Long userId;


    public String easyCreateTime;

    private User user;
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

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
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

    public Integer getIsSticky() {
        return isSticky;
    }

    public void setIsSticky(Integer isSticky) {
        this.isSticky = isSticky;
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
}