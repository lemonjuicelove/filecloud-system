package com.github.jfcloud.jos.core.operation.download.entity;

import lombok.Data;

import java.util.List;

@Data
public class DownloadFile {

    private String filename;
    private String path;
    private String metadata;

}
