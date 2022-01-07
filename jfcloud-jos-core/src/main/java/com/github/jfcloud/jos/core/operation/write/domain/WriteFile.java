package com.github.jfcloud.jos.core.operation.write.domain;

import lombok.Data;

@Data
public class WriteFile {
    private String fileUrl;
    private long fileSize;
}
