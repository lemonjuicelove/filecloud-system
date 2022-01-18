package com.github.jfcloud.jos.core.operation.delete.product;

import com.github.jfcloud.jos.core.config.LocalConfig;
import com.github.jfcloud.jos.core.operation.delete.Deleter;
import org.springframework.stereotype.Component;

/*
    本地存储实现类：文件删除
 */
@Component
public class LocalStorageDeleter implements Deleter {

    private LocalConfig localConfig;

    public LocalStorageDeleter() {
    }

    public LocalStorageDeleter(LocalConfig localConfig) {
        this.localConfig = localConfig;
    }

    // 删除文件
    @Override
    public void delete() {

    }

}
