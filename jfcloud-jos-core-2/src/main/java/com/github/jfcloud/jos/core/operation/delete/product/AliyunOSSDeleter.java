package com.github.jfcloud.jos.core.operation.delete.product;

import com.github.jfcloud.jos.core.config.AliyunConfig;
import com.github.jfcloud.jos.core.operation.delete.Deleter;

public class AliyunOSSDeleter implements Deleter {

    private AliyunConfig aliyunConfig;

    public AliyunOSSDeleter() {
    }

    public AliyunOSSDeleter(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public void delete() {

    }

}
