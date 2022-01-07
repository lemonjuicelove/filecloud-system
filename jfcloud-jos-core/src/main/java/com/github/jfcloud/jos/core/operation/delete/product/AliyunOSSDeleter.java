package com.github.jfcloud.jos.core.operation.delete.product;

import com.aliyun.oss.OSS;
import com.github.jfcloud.jos.core.config.AliyunConfig;
import com.github.jfcloud.jos.core.operation.delete.Deleter;
import com.github.jfcloud.jos.core.operation.delete.domain.DeleteFile;
import com.github.jfcloud.jos.core.util.AliyunUtils;
import com.github.jfcloud.jos.core.util.CusFileUtils;


public class AliyunOSSDeleter extends Deleter {
    private AliyunConfig aliyunConfig;

    public AliyunOSSDeleter(){

    }

    public AliyunOSSDeleter(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }
    @Override
    public void delete(DeleteFile deleteFile) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        try {
            ossClient.deleteObject(aliyunConfig.getOss().getBucketName(), CusFileUtils.getAliyunObjectNameByFileUrl(deleteFile.getFileUrl()));
        } finally {
            ossClient.shutdown();
        }
        deleteCacheFile(deleteFile);
    }
}
