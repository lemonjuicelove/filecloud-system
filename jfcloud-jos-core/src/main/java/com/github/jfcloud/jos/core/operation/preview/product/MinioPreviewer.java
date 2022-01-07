package com.github.jfcloud.jos.core.operation.preview.product;

import com.github.jfcloud.jos.core.config.MinioConfig;
import com.github.jfcloud.jos.core.domain.ThumbImage;
import com.github.jfcloud.jos.core.operation.preview.Previewer;
import com.github.jfcloud.jos.core.operation.preview.domain.PreviewFile;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Getter
@Setter
@Slf4j
public class MinioPreviewer extends Previewer {
    private MinioConfig minioConfig;

    public MinioPreviewer(){

    }

    public MinioPreviewer(MinioConfig minioConfig, ThumbImage thumbImage) {
        setMinioConfig(minioConfig);
        setThumbImage(thumbImage);
    }

    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        InputStream inputStream = null;
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(getMinioConfig().getEndpoint(), getMinioConfig().getAccessKey(), getMinioConfig().getSecretKey());
            minioClient.statObject(minioConfig.getBucketName(), previewFile.getFileUrl());
            inputStream = minioClient.getObject(minioConfig.getBucketName(), previewFile.getFileUrl());

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage());
        }


        return inputStream;
    }


}
