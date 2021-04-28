package com.example.cloud.common.enums;

/**
 * @author 言曌
 * @date 2018/5/31 下午9:07
 */

public enum NoticeTypeEnum {
    COMMENT_ARTICLE(1L, "评论文章"),
    REPLY_COMMENT(2L, "回复评论"),
    FOLLOW_YOU(3L, "关注了你"),
    ANSWER_QUESTION(4L, "回答问题"),
    COMMENT_ANSWER(5L, "评论回答"),
    ACCEPT_ANSWER(6L, "采纳答案");

    private Long code;

    private String msg;

    NoticeTypeEnum(Long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
