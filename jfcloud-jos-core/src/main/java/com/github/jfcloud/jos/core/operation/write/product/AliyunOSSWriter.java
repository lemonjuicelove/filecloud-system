package com.github.jfcloud.jos.core.operation.write.product;

import com.aliyun.oss.OSS;
import com.github.jfcloud.jos.core.config.AliyunConfig;
import com.github.jfcloud.jos.core.operation.write.Writer;
import com.github.jfcloud.jos.core.operation.write.domain.WriteFile;
import com.github.jfcloud.jos.core.util.AliyunUtils;
import com.github.jfcloud.jos.core.util.CusFileUtils;

import java.io.InputStream;

public class AliyunOSSWriter extends Writer {

    private AliyunConfig aliyunConfig;

    public AliyunOSSWriter(){

    }

    public AliyunOSSWriter(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);

        ossClient.putObject(aliyunConfig.getOss().getBucketName(), CusFileUtils.getAliyunObjectNameByFileUrl(writeFile.getFileUrl()), inputStream);
        ossClient.shutdown();
    }



}
