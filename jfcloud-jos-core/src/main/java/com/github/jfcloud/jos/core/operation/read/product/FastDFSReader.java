package com.github.jfcloud.jos.core.operation.read.product;

import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.jfcloud.jos.core.exception.operation.ReadException;
import com.github.jfcloud.jos.core.operation.read.Reader;
import com.github.jfcloud.jos.core.operation.read.domain.ReadFile;
import com.github.jfcloud.jos.core.util.ReadFileUtils;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FastDFSReader extends Reader {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Override
    public String read(ReadFile readFile) {

        String fileUrl = readFile.getFileUrl();
        String fileType = CusFileUtils.getFileExtendName(fileUrl);
        try {
            return ReadFileUtils.getContentByInputStream(fileType, getInputStream(readFile.getFileUrl()));
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        }
    }

    public InputStream getInputStream(String fileUrl) {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        group = "group1";
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, downloadByteArray);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return inputStream;
    }
}
