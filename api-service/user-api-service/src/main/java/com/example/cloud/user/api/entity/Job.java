package com.example.cloud.user.api.entity;

import java.io.Serializable;

/**
 * 职业表(Job)实体类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
public class Job implements Serializable {
    private static final long serialVersionUID = -55005002672336018L;
    /**
    * ID
    */
    private Long id;
    /**
    * 名称
    */
    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}