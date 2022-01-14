package com.github.jfcloud.jos.core.operation.write.product;

import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.jfcloud.jos.core.operation.write.Writer;
import com.github.jfcloud.jos.core.operation.write.domain.WriteFile;
import com.github.tobato.fastdfs.service.DefaultAppendFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

@Slf4j
public class FastDFSWriter extends Writer {
//    @Resource
    AppendFileStorageClient defaultAppendFileStorageClient = new DefaultAppendFileStorageClient();
    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        defaultAppendFileStorageClient.modifyFile("group1", writeFile.getFileUrl(), inputStream,
                writeFile.getFileSize(), 0);
    }
}
