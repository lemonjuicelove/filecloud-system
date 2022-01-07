package com.github.jfcloud.jos.core.constant;

/**
 * @author zj
 * @date 2021/12/31
 */
public enum  ResultCodeEnum {
    SUCCESS(true, 0, "成功"),
    UNKNOWN_ERROR(false, 999999, "未知错误"),
    DAO_INSERT_ERROR(false, 100000, "插入数据异常"),
    DAO_SELECT_ERROR(false, 100001, "查询数据异常"),
    DAO_UPDATE_ERROR(false, 100002, "更新数据异常"),
    DAO_DELETE_ERROR(false, 100003, "删除数据异常"),
    PARAM_ERROR(false, 200002, "参数错误"),
    NULL_POINT(false, 200003, "空指针异常"),
    INDEX_OUT_OF_BOUNDS(false, 200004, "下标越界异常"),
    REQUEST_TIMEOUT(false, 200005, "请求超时"),
    NOT_LOGIN_ERROR(false, 200006, "未登录异常");

    private Boolean success;
    private Integer code;
    private String message;

    private ResultCodeEnum(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
