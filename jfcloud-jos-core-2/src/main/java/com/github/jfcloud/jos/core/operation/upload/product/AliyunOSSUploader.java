package com.github.jfcloud.jos.core.operation.upload.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.github.jfcloud.jos.core.common.StorageTypeEnum;
import com.github.jfcloud.jos.core.common.UploadFileStatusEnum;
import com.github.jfcloud.jos.core.config.AliyunConfig;
import com.github.jfcloud.jos.core.operation.upload.Uploader;
import com.github.jfcloud.jos.core.operation.upload.entity.UploadFile;
import com.github.jfcloud.jos.core.operation.upload.entity.UploadFileResults;
import com.github.jfcloud.jos.core.util.AliyunUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AliyunOSSUploader implements Uploader {

    private AliyunConfig aliyunConfig;

    public AliyunOSSUploader() {
    }

    public AliyunOSSUploader(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    // 分片上传
    @Override
    public UploadFileResults upload(UploadFile uploadFile) {

        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        String objectName = uploadFile.getWholeIdentifier();
        String uploadId = uploadFile.getUploadId();
        if (uploadId == null || "".equals(uploadId)){ // 说明是第一个切片，初始化
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(aliyunConfig.getOss().getBucketName(), objectName);
            InitiateMultipartUploadResult uploadResult = ossClient.initiateMultipartUpload(request);
            uploadId = uploadResult.getUploadId();
        }

        // 上传切片文件
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(aliyunConfig.getOss().getBucketName());
        uploadPartRequest.setKey(objectName);
        uploadPartRequest.setUploadId(uploadId);
        InputStream is = null;

        try {
            is = uploadFile.getFile().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadPartRequest.setInputStream(is);
        uploadPartRequest.setPartNumber(uploadFile.getChunkNumber());
        UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
        List<PartETag> partETags = uploadFile.getPartETags();
        if (partETags == null) partETags = new ArrayList<>();
        partETags.add(uploadPartResult.getPartETag());

        UploadFileResults result = new UploadFileResults();
        result.setStorageType(StorageTypeEnum.ALIYUN_OSS);
        result.setFileName(uploadFile.getWholeIdentifier());
        result.setUploadId(uploadId);
        result.setPartETags(partETags);

        // 所有切片都上传成功
        if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()){
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(aliyunConfig.getOss().getBucketName(), objectName, uploadId, partETags);
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            result.setStatus(UploadFileStatusEnum.SUCCESS);
            for (PartETag partETag : partETags) {
                result.setFileSize(result.getFileSize()+partETag.getPartSize());
            }
            result.setFileUrl("https://"+aliyunConfig.getOss().getBucketName()+"."+aliyunConfig.getOss().getEndpoint()+"/" + objectName);
        }else{ // 不是最后一片切片
            result.setStatus(UploadFileStatusEnum.UNCOMPLATE);
        }

        ossClient.shutdown();

        return result;
    }


}
