package com.github.jfcloud.jos.core.operation.download.domain;

import com.aliyun.oss.OSS;
import lombok.Data;

@Data
public class DownloadFile {

    private String fileUrl;
    private long fileSize;
    private OSS ossClient;

}
