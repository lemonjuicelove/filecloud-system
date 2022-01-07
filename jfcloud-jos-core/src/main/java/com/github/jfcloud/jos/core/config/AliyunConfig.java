package com.github.jfcloud.jos.core.config;

import com.github.jfcloud.jos.core.domain.AliyunOSS;
import lombok.Data;

@Data
public class  AliyunConfig {
    private AliyunOSS oss = new AliyunOSS();


}
