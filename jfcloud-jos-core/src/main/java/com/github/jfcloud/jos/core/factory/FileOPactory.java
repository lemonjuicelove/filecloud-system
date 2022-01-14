package com.github.jfcloud.jos.core.factory;

import com.github.jfcloud.jos.core.autoconfiguration.JfleConfProperties;
import com.github.jfcloud.jos.core.config.AliyunConfig;
import com.github.jfcloud.jos.core.config.MinioConfig;
import com.github.jfcloud.jos.core.config.QiniuyunConfig;
import com.github.jfcloud.jos.core.constant.StorageTypeEnum;
import com.github.jfcloud.jos.core.domain.ThumbImage;
import com.github.jfcloud.jos.core.operation.copy.Copier;
import com.github.jfcloud.jos.core.operation.copy.product.*;
import com.github.jfcloud.jos.core.operation.delete.Deleter;
import com.github.jfcloud.jos.core.operation.delete.product.*;
import com.github.jfcloud.jos.core.operation.download.Downloader;
import com.github.jfcloud.jos.core.operation.download.product.*;
import com.github.jfcloud.jos.core.operation.preview.Previewer;
import com.github.jfcloud.jos.core.operation.preview.product.*;
import com.github.jfcloud.jos.core.operation.read.Reader;
import com.github.jfcloud.jos.core.operation.read.product.*;
import com.github.jfcloud.jos.core.operation.upload.Uploader;
import com.github.jfcloud.jos.core.operation.upload.product.*;
import com.github.jfcloud.jos.core.operation.write.Writer;
import com.github.jfcloud.jos.core.operation.write.product.*;

import javax.annotation.Resource;

public class FileOPactory {

    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyunConfig;
    private ThumbImage thumbImage;
    private MinioConfig minioConfig;
    private QiniuyunConfig qiniuyunConfig;

    @Resource
    private FastDFSCopier fastDFSCopier;
    @Resource
    private FastDFSUploader fastDFSUploader;
    @Resource
    private FastDFSDownloader fastDFSDownloader;
    @Resource
    private FastDFSDeleter fastDFSDeleter;
    @Resource
    private FastDFSReader fastDFSReader;
    @Resource
    private FastDFSPreviewer fastDFSPreviewer;
    @Resource
    private FastDFSWriter fastDFSWriter;
    @Resource
    private AliyunOSSUploader aliyunOSSUploader;
    @Resource
    private MinioUploader minioUploader;
    @Resource
    private QiniuyunKodoUploader qiniuyunKodoUploader;

    public FileOPactory() {
    }

    public FileOPactory(JfleConfProperties jfleConfProperties) {
        this.storageType = jfleConfProperties.getStorageType();
        this.localStoragePath = jfleConfProperties.getLocalStoragePath();
        this.aliyunConfig = jfleConfProperties.getAliyun();
        this.thumbImage = jfleConfProperties.getThumbImage();
        this.minioConfig = jfleConfProperties.getMinio();
        this.qiniuyunConfig = jfleConfProperties.getQiniuyun();
    }

    public Uploader getUploader() {

        int type = Integer.parseInt(storageType);
        Uploader uploader = null;
        if (StorageTypeEnum.LOCAL.getCode() == type) {
            uploader = new LocalStorageUploader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type) {
            uploader = aliyunOSSUploader;
        } else if (StorageTypeEnum.FAST_DFS.getCode() == type) {
            uploader = fastDFSUploader;
        } else if (StorageTypeEnum.MINIO.getCode() == type) {
            uploader = minioUploader;
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == type) {
            uploader = qiniuyunKodoUploader;
        }
        return uploader;
    }


    public Downloader getDownloader(int storageType) {
        Downloader downloader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            downloader = new LocalStorageDownloader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            downloader = new AliyunOSSDownloader(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            downloader = fastDFSDownloader;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            downloader = new MinioDownloader(minioConfig);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            downloader = new QiniuyunKodoDownloader(qiniuyunConfig);
        }
        return downloader;
    }

    public Deleter getDeleter(int storageType) {
        Deleter deleter = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            deleter = new LocalStorageDeleter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            deleter = new AliyunOSSDeleter(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            deleter = fastDFSDeleter;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            deleter = new MinioDeleter(minioConfig);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            deleter = new QiniuyunKodoDeleter(qiniuyunConfig);
        }
        return deleter;
    }

    public Reader getReader(int storageType) {
        Reader reader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            reader = new LocalStorageReader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            reader = new AliyunOSSReader(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            reader = fastDFSReader;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            reader = new MinioReader(minioConfig);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            reader = new QiniuyunKodoReader(qiniuyunConfig);
        }
        return reader;
    }

    public Writer getWriter(int storageType) {
        Writer writer = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            writer = new LocalStorageWriter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            writer = new AliyunOSSWriter(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            writer = fastDFSWriter;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            writer = new MinioWriter(minioConfig);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            writer = new QiniuyunKodoWriter(qiniuyunConfig);
        }
        return writer;
    }

    public Previewer getPreviewer(int storageType) {
        Previewer previewer = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            previewer = new LocalStoragePreviewer(thumbImage);
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            previewer = new AliyunOSSPreviewer(aliyunConfig, thumbImage);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            previewer = fastDFSPreviewer;
        } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
            previewer = new MinioPreviewer(minioConfig, thumbImage);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
            previewer = new QiniuyunKodoPreviewer(qiniuyunConfig, thumbImage);
        }
        return previewer;
    }

    public Copier getCopier() {
        int type = Integer.parseInt(storageType);
        Copier copier = null;
        if (StorageTypeEnum.LOCAL.getCode() == type) {
            copier = new LocalStorageCopier();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type) {
            copier = new AliyunOSSCopier(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == type) {
            copier = fastDFSCopier;
        } else if (StorageTypeEnum.MINIO.getCode() == type) {
            copier = new MinioCopier(minioConfig);
        } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == type) {
            copier = new QiniuyunKodoCopier(qiniuyunConfig);
        }
        return copier;
    }

}
