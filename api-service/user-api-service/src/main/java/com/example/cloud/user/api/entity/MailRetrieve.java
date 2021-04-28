package com.example.cloud.user.api.entity;

import java.io.Serializable;

/**
 * 邮件记录表(MailRetrieve)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public class MailRetrieve implements Serializable {
    private static final long serialVersionUID = 455313083935286507L;
    /**
    * ID
    */
    private Long id;
    /**
    * 用户ID
    */
    private String account;
    /**
    * 创建时间
    */
    private Long createTime;
    /**
    * 过期时间
    */
    private Long outTime;
    /**
    * 自定义ID
    */
    private String sid;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getOutTime() {
        return outTime;
    }

    public void setOutTime(Long outTime) {
        this.outTime = outTime;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public MailRetrieve() {
    }

    public MailRetrieve(String account, String sid, long outTime) {
        this.account = account;
        this.sid = sid;
        this.outTime = outTime;
    }


}