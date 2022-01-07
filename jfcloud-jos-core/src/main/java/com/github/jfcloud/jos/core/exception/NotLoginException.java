package com.github.jfcloud.jos.core.exception;

/**
 * @author zj
 * @date 2021/12/31
 */
public class NotLoginException extends RuntimeException{
    public NotLoginException() {
        super("未登录");
    }

    public NotLoginException(Throwable cause) {
        super("未登录", cause);
    }

    public NotLoginException(String message) {
        super(message);
    }

    public NotLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
