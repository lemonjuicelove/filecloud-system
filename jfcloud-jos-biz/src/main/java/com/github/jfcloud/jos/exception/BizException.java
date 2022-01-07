package com.github.jfcloud.jos.exception;


/**
 * 自定义异常
 * @author zj
 * @date 2021/8/26
 */
public class BizException extends RuntimeException {


    private static final long serialVersionUID = 526893094066566535L;
    /**
     * 错误码
     */
    protected Integer errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public BizException(String errorMsg) {
        super(errorMsg);
        this.errorCode = 500;
        this.errorMsg = errorMsg;
    }

    public BizException(Integer errorCode, String errorMsg) {
        super(errorCode.toString());
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BizException(Integer errorCode, String errorMsg, Throwable cause) {
        super(errorCode.toString(), cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String getMessage() {
        return errorMsg;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}

