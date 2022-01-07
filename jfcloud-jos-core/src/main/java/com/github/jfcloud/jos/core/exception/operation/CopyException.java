package com.github.jfcloud.jos.core.exception.operation;

import com.github.jfcloud.jos.core.exception.CommException;

public class CopyException extends CommException {
    public CopyException(Throwable cause) {
        super("创建出现了异常", cause);
    }

    public CopyException(String message) {
        super(message);
    }

    public CopyException(String message, Throwable cause) {
        super(message, cause);
    }

}
