package com.github.jfcloud.jos.core.domain;

import lombok.Data;

@Data
public class AliyunOSS {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String objectName;
}
