package com.github.jfcloud.jos.core.operation.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public abstract class Uploader {

    // 文件上传
    public abstract void upload(MultipartFile file, String dirname, String filename);

    // 合并切片
    public abstract File mergeFile(String filename, String metadata);



}
