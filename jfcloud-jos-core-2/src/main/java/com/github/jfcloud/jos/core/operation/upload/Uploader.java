package com.github.jfcloud.jos.core.operation.upload;

import com.aliyun.oss.model.UploadFileResult;
import com.github.jfcloud.jos.core.operation.upload.entity.UploadFile;
import com.github.jfcloud.jos.core.operation.upload.entity.UploadFileResults;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface Uploader {

    // 文件上传
    UploadFileResults upload(UploadFile uploadFile);

    // 合并切片
    // public abstract File mergeFile(String filename, String metadata);



}
