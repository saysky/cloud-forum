package com.example.cloud.user.api.entity;

import java.io.Serializable;

/**
 * 粉丝关系表(Relationship)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public class Relationship implements Serializable {
    private static final long serialVersionUID = -23360630958738274L;
    /**
    * 我方ID
    */
    private Long toUserId;
    /**
    * 对方ID
    */
    private Long fromUserId;


    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Relationship() {
    }

    public Relationship(Long toUserId, Long fromUserId) {
        this.toUserId = toUserId;
        this.fromUserId = fromUserId;
    }
}