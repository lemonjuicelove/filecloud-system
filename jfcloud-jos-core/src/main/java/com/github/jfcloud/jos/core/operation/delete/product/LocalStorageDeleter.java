package com.github.jfcloud.jos.core.operation.delete.product;

import com.github.jfcloud.jos.core.exception.operation.DeleteException;
import com.github.jfcloud.jos.core.operation.delete.Deleter;
import com.github.jfcloud.jos.core.operation.delete.domain.DeleteFile;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        File localSaveFile = CusFileUtils.getLocalSaveFile(deleteFile.getFileUrl());
        if (localSaveFile.exists()) {
            boolean result = localSaveFile.delete();
            if (!result) {
                new DeleteException("删除本地文件失败");
            }
        }

        deleteCacheFile(deleteFile);
    }
}
