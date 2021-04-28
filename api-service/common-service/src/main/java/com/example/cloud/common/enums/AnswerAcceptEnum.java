package com.example.cloud.common.enums;

/**
 * @author 言曌
 * @date 2018/6/5 下午4:12
 */

public enum AnswerAcceptEnum {
    ACCEPT_STATUS(1,"已采纳"),
    NOT_ACCEPT_STATUS(0,"未采纳")
    ;

    private Integer code;

    private String message;

    AnswerAcceptEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
