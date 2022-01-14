package com.github.jfcloud.jos.core.common;


public enum StorageTypeEnum {

    LOCAL(0, "本地存储"),
    ALIYUN_OSS(1, "阿里云OSS对象存储"),
    FAST_DFS(2, "fastDFS集群存储");

    private int code;
    private String name;

    StorageTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

}
