package com.github.jfcloud.jos.core.operation.download;

import com.github.jfcloud.jos.core.operation.download.entity.DownloadFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Downloader {

    // 文件下载
    void download(DownloadFile downloadFile, HttpServletRequest request, HttpServletResponse response);

    // 批量文件下载
    // void downloadBatch(List<DownloadFile> downloadFiles, HttpServletRequest request, HttpServletResponse response);

    InputStream InputStream(DownloadFile downloadFile);
}
