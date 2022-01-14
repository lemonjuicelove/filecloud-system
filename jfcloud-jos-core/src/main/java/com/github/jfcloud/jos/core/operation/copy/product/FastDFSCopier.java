package com.github.jfcloud.jos.core.operation.copy.product;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.jfcloud.jos.core.operation.copy.Copier;
import com.github.jfcloud.jos.core.operation.copy.domain.CopyFile;
import com.github.tobato.fastdfs.service.DefaultAppendFileStorageClient;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;


public class FastDFSCopier extends Copier {


    private AppendFileStorageClient defaultAppendFileStorageClient = new DefaultAppendFileStorageClient();

    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        StorePath storePath = new StorePath();
        try {
            storePath = defaultAppendFileStorageClient.uploadAppenderFile("group1", inputStream,
                    inputStream.available(), copyFile.getExtendName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return storePath.getPath();
    }
}
