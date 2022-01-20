package com.github.jfcloud.jos.core.common;

import com.github.jfcloud.jos.core.constant.ResultCodeEnum;

/**
 * @author zj
 * @date 2021/12/31
 */
public class RestResult<T> {

    private Boolean success = true;
    private Integer code = 0;
    private String message;
    private T data;

    public static RestResult success() {
        RestResult r = new RestResult();
        r.setSuccess(ResultCodeEnum.SUCCESS.getSuccess());
        r.setCode(ResultCodeEnum.SUCCESS.getCode());
        r.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return r;
    }

    public static RestResult fail() {
        RestResult r = new RestResult();
        r.setSuccess(ResultCodeEnum.UNKNOWN_ERROR.getSuccess());
        r.setCode(ResultCodeEnum.UNKNOWN_ERROR.getCode());
        r.setMessage(ResultCodeEnum.UNKNOWN_ERROR.getMessage());
        return r;
    }

    public static RestResult setResult(ResultCodeEnum result) {
        RestResult r = new RestResult();
        r.setSuccess(result.getSuccess());
        r.setCode(result.getCode());
        r.setMessage(result.getMessage());
        return r;
    }

    public RestResult data(T param) {
        this.setData(param);
        return this;
    }

    public RestResult message(String message) {
        this.setMessage(message);
        return this;
    }

    public RestResult code(Integer code) {
        this.setCode(code);
        return this;
    }

    public RestResult success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public RestResult() {
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

    public T getData() {
        return this.data;
    }

    public void setSuccess(final Boolean success) {
        this.success = success;
    }

    public void setCode(final Integer code) {
        this.code = code;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setData(final T data) {
        this.data = data;
    }

}
