package com.github.jfcloud.jos.core.operation.preview.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.github.jfcloud.jos.core.config.AliyunConfig;
import com.github.jfcloud.jos.core.domain.ThumbImage;
import com.github.jfcloud.jos.core.operation.preview.Previewer;
import com.github.jfcloud.jos.core.operation.preview.domain.PreviewFile;
import com.github.jfcloud.jos.core.util.AliyunUtils;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Data
@Slf4j
public class AliyunOSSPreviewer extends Previewer {


    private AliyunConfig aliyunConfig;

    public AliyunOSSPreviewer(){

    }

    public AliyunOSSPreviewer(AliyunConfig aliyunConfig, ThumbImage thumbImage) {
        this.aliyunConfig = aliyunConfig;
        setThumbImage(thumbImage);
    }


    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                CusFileUtils.getAliyunObjectNameByFileUrl(previewFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();
        previewFile.setOssClient(ossClient);
        return inputStream;
    }

}
