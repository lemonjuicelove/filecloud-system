package com.github.jfcloud.jos.core.config;

import lombok.Data;

@Data
public class LocalConfig {

    private String realPath; // 真实存储路径
    private String tempPath; // 临时存储路径
    private String emptyfilepath; // 空文件的路径

    private static final String EMPTYMD5 = "d41d8cd98f00b204e9800998ecf8427e";
    private static final String EMPTYSHA1 = "da39a3ee5e6b4b0d3255bfef95601890afd80709";
    private static final String EMPTYCRC32 = "0";

}
