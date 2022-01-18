package com.github.jfcloud.jos.core.common;

public enum UploadFileStatusEnum {

    FAIL(0, "上传失败"),
    SUCCESS(1, "上传成功"),
    UNCOMPLATE(2, "未完成");

    private int code;
    private String message;

    UploadFileStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
