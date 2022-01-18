package com.github.jfcloud.jos.core.factory;

import com.github.jfcloud.jos.core.autoconfiguration.JosCoreFileConfProperties;
import com.github.jfcloud.jos.core.common.StorageTypeEnum;
import com.github.jfcloud.jos.core.config.AliyunConfig;
import com.github.jfcloud.jos.core.config.LocalConfig;
import com.github.jfcloud.jos.core.operation.delete.Deleter;
import com.github.jfcloud.jos.core.operation.delete.product.AliyunOSSDeleter;
import com.github.jfcloud.jos.core.operation.delete.product.LocalStorageDeleter;
import com.github.jfcloud.jos.core.operation.download.Downloader;
import com.github.jfcloud.jos.core.operation.download.product.AliyunOSSDownloader;
import com.github.jfcloud.jos.core.operation.download.product.LocalStorageDownloader;
import com.github.jfcloud.jos.core.operation.upload.Uploader;
import com.github.jfcloud.jos.core.operation.upload.product.AliyunOSSUploader;
import com.github.jfcloud.jos.core.operation.upload.product.LocalStorageUploader;


public class  FileOperatorFactory {

    private String storageType;
    private LocalConfig localConfig;
    private AliyunConfig aliyunConfig;

    public FileOperatorFactory() {

    }

    public FileOperatorFactory(JosCoreFileConfProperties josCoreFileConfProperties) {
        storageType = josCoreFileConfProperties.getStorageType();
        localConfig = josCoreFileConfProperties.getLocal();
        aliyunConfig = josCoreFileConfProperties.getAliyun();
    }

    public Uploader getUploader(){
        int type = Integer.parseInt(storageType);
        Uploader uploader = null;
        if (StorageTypeEnum.LOCAL.getCode() == type){
            uploader = new LocalStorageUploader(localConfig);
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type){
            uploader = new AliyunOSSUploader(aliyunConfig);
        }else if (StorageTypeEnum.FAST_DFS.getCode() == type){

        }
        return uploader;
    }

    public Downloader getDownloader(){
        int type = Integer.parseInt(storageType);
        Downloader downloader = null;
        if (StorageTypeEnum.LOCAL.getCode() == type){
            downloader = new LocalStorageDownloader();
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type){
            downloader = new AliyunOSSDownloader(aliyunConfig);
        }else if (StorageTypeEnum.FAST_DFS.getCode() == type){

        }
        return downloader;
    }

    public Deleter getDeleter(){
        int type = Integer.parseInt(storageType);
        Deleter deleter = null;
        if (StorageTypeEnum.LOCAL.getCode() == type){
            deleter = new LocalStorageDeleter(localConfig);
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type){
            deleter = new AliyunOSSDeleter(aliyunConfig);
        }else if(StorageTypeEnum.FAST_DFS.getCode() == type){

        }
        return deleter;
    }

    public Uploader getUploader(int storageType){
        Uploader uploader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType){
            uploader = new LocalStorageUploader(localConfig);
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType){
            uploader = new AliyunOSSUploader(aliyunConfig);
        }else if (StorageTypeEnum.FAST_DFS.getCode() == storageType){

        }
        return uploader;
    }

    public Downloader getDownloader(int storageType){
        Downloader downloader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType){
            downloader = new LocalStorageDownloader();
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType){
            downloader = new AliyunOSSDownloader(aliyunConfig);
        }else if (StorageTypeEnum.FAST_DFS.getCode() == storageType){

        }
        return downloader;
    }

    public Deleter getDeleter(int storageType){
        Deleter deleter = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType){
            deleter = new LocalStorageDeleter(localConfig);
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType){
            deleter = new AliyunOSSDeleter(aliyunConfig);
        }else if(StorageTypeEnum.FAST_DFS.getCode() == storageType){

        }
        return deleter;
    }

}
