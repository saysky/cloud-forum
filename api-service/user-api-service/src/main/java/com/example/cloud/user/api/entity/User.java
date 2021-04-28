package com.example.cloud.user.api.entity;

import com.example.cloud.common.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表(User)实体类
 *
 * @author makejava
 * @since 2021-03-30 14:44:35
 */
public class User implements Serializable {
    private static final long serialVersionUID = 494390020069936730L;
    /**
    * ID
    */
    private Long id;
    /**
    * 回答数
    */
    private Long answerSize;
    /**
    * 文章数
    */
    private Long articleSize;
    /**
    * 头像URL
    */
    private String avatar;
    /**
    * 联系方式
    */
    private String contact;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 电子邮箱
    */
    private String email;
    /**
    * 粉丝数
    */
    private Long fanSize;
    /**
    * 关注数
    */
    private Long followSize;
    /**
    * GitHub
    */
    private String github;
    /**
    * 个人主页
    */
    private String homepage;
    /**
    * 是否验证了邮箱
    */
    private String isVerifyEmail;
    /**
    * 上次登录时间
    */
    private Date lastLoginTime;
    /**
    * 昵称
    */
    private String nickname;
    /**
    * 密码
    */
    private String password;
    /**
    * 个人信息
    */
    private String profile;
    /**
    * 提问数
    */
    private Long questionSize;
    /**
    * 声望
    */
    private Integer reputation;
    /**
    * 状态
    */
    private String status;
    /**
    * 用户名
    */
    private String username;
    /**
    * 阅读数
    */
    private Long viewSize;
    /**
    * 职业ID
    */
    private Long jobId;
    /**
    * 角色
    */
    private String role;

    /**
     * 非数据库字段
     */
    private Integer isFriend;

    private Job job;


    public String easyCreateTime;

    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnswerSize() {
        return answerSize;
    }

    public void setAnswerSize(Long answerSize) {
        this.answerSize = answerSize;
    }

    public Long getArticleSize() {
        return articleSize;
    }

    public void setArticleSize(Long articleSize) {
        this.articleSize = articleSize;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getFanSize() {
        return fanSize;
    }

    public void setFanSize(Long fanSize) {
        this.fanSize = fanSize;
    }

    public Long getFollowSize() {
        return followSize;
    }

    public void setFollowSize(Long followSize) {
        this.followSize = followSize;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getIsVerifyEmail() {
        return isVerifyEmail;
    }

    public void setIsVerifyEmail(String isVerifyEmail) {
        this.isVerifyEmail = isVerifyEmail;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Long getQuestionSize() {
        return questionSize;
    }

    public void setQuestionSize(Long questionSize) {
        this.questionSize = questionSize;
    }

    public Integer getReputation() {
        return reputation;
    }

    public void setReputation(Integer reputation) {
        this.reputation = reputation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getViewSize() {
        return viewSize;
    }

    public void setViewSize(Long viewSize) {
        this.viewSize = viewSize;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(Integer isFriend) {
        this.isFriend = isFriend;
    }
}