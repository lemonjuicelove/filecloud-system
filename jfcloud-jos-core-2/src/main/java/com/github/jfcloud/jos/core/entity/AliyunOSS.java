package com.github.jfcloud.jos.core.entity;

import lombok.Data;

@Data
public class AliyunOSS {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName; // 文件存储位置

}
