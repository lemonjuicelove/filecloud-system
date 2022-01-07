package com.github.jfcloud.jos.core.operation.preview.product;

import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.jfcloud.jos.core.domain.ThumbImage;
import com.github.jfcloud.jos.core.operation.preview.Previewer;
import com.github.jfcloud.jos.core.operation.preview.domain.PreviewFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
public class FastDFSPreviewer extends Previewer {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public FastDFSPreviewer(){}

    public FastDFSPreviewer(ThumbImage thumbImage) {

        setThumbImage(thumbImage);
    }

    protected InputStream getInputStream(PreviewFile previewFile) {
        String group = "group1";
        String path = previewFile.getFileUrl().substring(previewFile.getFileUrl().indexOf("/") + 1);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, downloadByteArray);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return inputStream;
    }

}
