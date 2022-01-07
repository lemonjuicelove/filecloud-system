package com.github.jfcloud.jos.core.autoconfiguration;

import com.github.jfcloud.jos.core.config.AliyunConfig;
import com.github.jfcloud.jos.core.config.MinioConfig;
import com.github.jfcloud.jos.core.config.QiniuyunConfig;
import com.github.jfcloud.jos.core.domain.ThumbImage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jfconf")
public class JfleConfProperties {

    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyun = new AliyunConfig();
    private ThumbImage thumbImage = new ThumbImage();
    private MinioConfig minio = new MinioConfig();
    private QiniuyunConfig qiniuyun = new QiniuyunConfig();
}
