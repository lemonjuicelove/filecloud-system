package com.github.jfcloud.jos.core.operation.read.product;

import com.github.jfcloud.jos.core.exception.operation.ReadException;
import com.github.jfcloud.jos.core.operation.read.Reader;
import com.github.jfcloud.jos.core.operation.read.domain.ReadFile;
import com.github.jfcloud.jos.core.util.ReadFileUtils;
import com.github.jfcloud.jos.core.util.CusFileUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class LocalStorageReader extends Reader {
    @Override
    public String read(ReadFile readFile) {

        String fileContent;
        try {
            String extendName = CusFileUtils.getFileExtendName(readFile.getFileUrl());
            FileInputStream fileInputStream = new FileInputStream(CusFileUtils.getStaticPath() + readFile.getFileUrl());
            fileContent = ReadFileUtils.getContentByInputStream(extendName, fileInputStream);
        } catch (IOException e) {
            throw new ReadException("文件读取出现异常", e);
        }
        return fileContent;
    }
}
