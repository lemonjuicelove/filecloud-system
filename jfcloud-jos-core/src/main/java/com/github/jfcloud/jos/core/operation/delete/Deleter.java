package com.github.jfcloud.jos.core.operation.delete;

import com.github.jfcloud.jos.core.operation.delete.domain.DeleteFile;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public abstract class Deleter {
    public abstract void delete(DeleteFile deleteFile);

    protected void deleteCacheFile(DeleteFile deleteFile) {
        if (CusFileUtils.isImageFile(CusFileUtils.getFileExtendName(deleteFile.getFileUrl()))) {
            File cacheFile = CusFileUtils.getCacheFile(deleteFile.getFileUrl());
            if (cacheFile.exists()) {
                boolean result = cacheFile.delete();
                if (!result) {
                    log.error("删除本地缓存文件失败！");
                }
            }
        }
    }
}
