package com.github.jfcloud.jos.core.domain;

import lombok.Data;

@Data
public class QiniuyunKodo {
    private String domain;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
