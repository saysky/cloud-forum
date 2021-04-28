package com.example.cloud.qa.api.entity;

import java.io.Serializable;

/**
 * 回答和踩关联表(AnswerCai)实体类
 *
 * @author makejava
 * @since 2021-03-30 14:44:35
 */
public class AnswerCai implements Serializable {
    private static final long serialVersionUID = -76245041705792148L;
    /**
    * 回答ID
    */
    private Long answerId;
    /**
    * 用户ID
    */
    private Long userId;
    /**
    * 主键
    */
    private Long id;


    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
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