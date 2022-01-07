package com.github.jfcloud.jos.core.autoconfiguration;

import com.github.jfcloud.jos.core.factory.FileOPactory;
import com.github.jfcloud.jos.core.operation.copy.product.FastDFSCopier;
import com.github.jfcloud.jos.core.operation.delete.product.FastDFSDeleter;
import com.github.jfcloud.jos.core.operation.download.product.FastDFSDownloader;
import com.github.jfcloud.jos.core.operation.preview.product.FastDFSPreviewer;
import com.github.jfcloud.jos.core.operation.read.product.FastDFSReader;
import com.github.jfcloud.jos.core.operation.upload.product.AliyunOSSUploader;
import com.github.jfcloud.jos.core.operation.upload.product.FastDFSUploader;
import com.github.jfcloud.jos.core.operation.upload.product.MinioUploader;
import com.github.jfcloud.jos.core.operation.upload.product.QiniuyunKodoUploader;
import com.github.jfcloud.jos.core.operation.write.product.FastDFSWriter;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import com.github.jfcloud.jos.core.util.RedisUtil;
import com.github.jfcloud.jos.core.util.concurrent.locks.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@EnableConfigurationProperties({JfleConfProperties.class})
public class JosCoreFileAutoConfiguration {

    @Autowired
    private JfleConfProperties jfleConfProperties;


    @Bean
    public FileOPactory ufopFactory() {
        CusFileUtils.LOCAL_STORAGE_PATH = jfleConfProperties.getLocalStoragePath();
        return new FileOPactory(jfleConfProperties);
    }
    @Bean
    public FastDFSCopier fastDFSCreater() {
        return new FastDFSCopier();
    }

    @Bean
    public FastDFSUploader fastDFSUploader() {
        return new FastDFSUploader();
    }

    @Bean
    public FastDFSDownloader fastDFSDownloader() {
        return new FastDFSDownloader();
    }

    @Bean
    public FastDFSDeleter fastDFSDeleter() {
        return new FastDFSDeleter();
    }

    @Bean
    public FastDFSReader fastDFSReader() {
        return new FastDFSReader();
    }

    @Bean
    public FastDFSWriter fastDFSWriter() {
        return new FastDFSWriter();
    }

    @Bean
    public FastDFSPreviewer fastDFSPreviewer() {
        return new FastDFSPreviewer(jfleConfProperties.getThumbImage());
    }

    @Bean
    public AliyunOSSUploader aliyunOSSUploader() {
        return new AliyunOSSUploader(jfleConfProperties.getAliyun());
    }

    @Bean
    public MinioUploader minioUploader() {
        return new MinioUploader(jfleConfProperties.getMinio());
    }

    @Bean
    public QiniuyunKodoUploader qiniuyunKodoUploader() {
        return new QiniuyunKodoUploader(jfleConfProperties.getQiniuyun());
    }

    @Bean
    public RedisLock redisLock() {
        return new RedisLock();
    }

    @Bean
    public RedisUtil redisUtil() {
        return new RedisUtil();
    }

}
