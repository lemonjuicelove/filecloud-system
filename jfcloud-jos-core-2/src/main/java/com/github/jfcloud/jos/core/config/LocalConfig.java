package com.github.jfcloud.jos.core.config;

import lombok.Data;

@Data
public class LocalConfig {

    private String realPath; // 真实存储路径
    private String tempPath; // 临时存储路径
    private String emptyFilePath; // 空文件的路径
    private final String emptyMD5 = "d41d8cd98f00b204e9800998ecf8427e"; // 空文件的md5值


}
