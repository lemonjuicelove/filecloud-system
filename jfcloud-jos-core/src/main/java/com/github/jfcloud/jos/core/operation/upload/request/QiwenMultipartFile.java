package com.github.jfcloud.jos.core.operation.upload.request;

import com.github.jfcloud.jos.core.util.CusFileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class QiwenMultipartFile {

    MultipartFile multipartFile = null;

    public QiwenMultipartFile() {
    }

    public QiwenMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getFileName() {

        String originalName = getMultipartFile().getOriginalFilename();
        if (!originalName.contains(".")) {
            return originalName;
        }
        return originalName.substring(0, originalName.lastIndexOf("."));
    }

    public String getExtendName() {
        String originalName = getMultipartFile().getOriginalFilename();
        String extendName = CusFileUtils.getFileExtendName(originalName);
        return extendName;
    }

    public String getFileUrl() {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = CusFileUtils.getUploadFileUrl(uuid, getExtendName());
        return fileUrl;
    }

    public String getFileUrl(String identify) {
        String fileUrl = CusFileUtils.getUploadFileUrl(identify, getExtendName());
        return fileUrl;
    }

    public InputStream getUploadInputStream() throws IOException {
        return getMultipartFile().getInputStream();
    }

    public byte[] getUploadBytes() throws IOException {
        return getMultipartFile().getBytes();
    }

    public long getSize() {
        long size = getMultipartFile().getSize();
        return size;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    private void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
