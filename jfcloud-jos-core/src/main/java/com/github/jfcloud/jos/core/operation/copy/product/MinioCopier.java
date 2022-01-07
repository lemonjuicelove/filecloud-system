package com.github.jfcloud.jos.core.operation.copy.product;

import com.github.jfcloud.jos.core.config.MinioConfig;
import com.github.jfcloud.jos.core.exception.operation.CopyException;
import com.github.jfcloud.jos.core.operation.copy.Copier;
import com.github.jfcloud.jos.core.operation.copy.domain.CopyFile;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.UUID;

public class MinioCopier extends Copier {

    private MinioConfig minioConfig;

    public MinioCopier(){

    }

    public MinioCopier(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = CusFileUtils.getUploadFileUrl(uuid, copyFile.getExtendName());

        // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        try {
            MinioClient minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(minioConfig.getBucketName());
            if(!isExist) {
                minioClient.makeBucket(minioConfig.getBucketName());
            }
            PutObjectOptions putObjectOptions = new PutObjectOptions(inputStream.available(), 1024 * 1024 * 5);
            // 使用putObject上传一个文件到存储桶中。
            minioClient.putObject(minioConfig.getBucketName(), fileUrl, inputStream, putObjectOptions);

        } catch (Exception e) {
            throw new CopyException("创建文件出现异常", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return fileUrl;
    }


}
