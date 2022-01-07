package com.github.jfcloud.jos.core.operation.preview.domain;

import com.aliyun.oss.OSS;
import lombok.Data;

@Data
public class PreviewFile {
    private String fileUrl;
    private long fileSize;
    private OSS ossClient;
}
