package com.github.jfcloud.jos.core.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.jfcloud.jos.core.config.AliyunConfig;

public class AliyunUtils {

    public static OSS getOSSClient(AliyunConfig aliyunConfig) {
        OSS ossClient = new OSSClientBuilder().build(aliyunConfig.getOss().getEndpoint(),
                aliyunConfig.getOss().getAccessKeyId(),
                aliyunConfig.getOss().getAccessKeySecret());;
        return ossClient;
    }

}
