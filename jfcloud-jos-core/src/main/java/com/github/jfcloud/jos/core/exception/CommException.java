package com.github.jfcloud.jos.core.exception;

public class CommException extends RuntimeException {
    public CommException(Throwable cause) {
        super("统一文件操作平台出现异常", cause);
    }

    public CommException(String message) {
        super(message);
    }

    public CommException(String message, Throwable cause) {
        super(message, cause);
    }

}
