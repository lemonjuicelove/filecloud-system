package com.github.jfcloud.jos.core.autoconfiguration;

import com.github.jfcloud.jos.core.config.LocalConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jfconf")
public class JosCoreFileConfProperties {

    // 存储类型
    private String storageType;

    // 本地存储
    private LocalConfig local = new LocalConfig();

}
