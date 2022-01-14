package com.github.jfcloud.jos.core.factory;

import com.github.jfcloud.jos.core.autoconfiguration.JosCoreFileConfProperties;
import com.github.jfcloud.jos.core.common.StorageTypeEnum;
import com.github.jfcloud.jos.core.config.LocalConfig;
import com.github.jfcloud.jos.core.operation.delete.Deleter;
import com.github.jfcloud.jos.core.operation.delete.product.LocalStorageDeleter;
import com.github.jfcloud.jos.core.operation.download.Downloader;
import com.github.jfcloud.jos.core.operation.download.product.LocalStorageDownloader;
import com.github.jfcloud.jos.core.operation.upload.Uploader;
import com.github.jfcloud.jos.core.operation.upload.product.LocalStorageUploader;


public class FileOperatorFactory {

    private String storageType;
    private LocalConfig localConfig;

    public FileOperatorFactory() {
    }

    public FileOperatorFactory(JosCoreFileConfProperties josCoreFileConfProperties) {
        storageType = josCoreFileConfProperties.getStorageType();
        localConfig = josCoreFileConfProperties.getLocal();
    }

    public Uploader getUploader(){
        int type = Integer.parseInt(storageType);
        Uploader uploader = null;
        if (type == StorageTypeEnum.LOCAL.getCode()){
            uploader = new LocalStorageUploader(localConfig);
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type){

        }else if (StorageTypeEnum.FAST_DFS.getCode() == type){

        }
        return uploader;
    }

    public Downloader getDownloader(){
        int type = Integer.parseInt(storageType);
        Downloader downloader = null;
        if (type == StorageTypeEnum.LOCAL.getCode()){
            downloader = new LocalStorageDownloader();
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type){

        }else if (StorageTypeEnum.FAST_DFS.getCode() == type){

        }
        return downloader;
    }

    public Deleter getDeleter(){
        int type = Integer.parseInt(storageType);
        Deleter deleter = null;
        if (type == StorageTypeEnum.LOCAL.getCode()){
            deleter = new LocalStorageDeleter();
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type){

        }else if(StorageTypeEnum.FAST_DFS.getCode() == type){

        }
        return deleter;
    }

    public Uploader getUploader(int storageType){
        Uploader uploader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType){
            uploader = new LocalStorageUploader();
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType){

        }else if (StorageTypeEnum.FAST_DFS.getCode() == storageType){

        }
        return uploader;
    }

    public Downloader getDownloader(int storageType){
        Downloader downloader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType){
            downloader = new LocalStorageDownloader();
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType){

        }else if (StorageTypeEnum.FAST_DFS.getCode() == storageType){

        }
        return downloader;
    }

    public Deleter getDeleter(int storageType){
        Deleter deleter = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType){
            deleter = new LocalStorageDeleter();
        }else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType){

        }else if(StorageTypeEnum.FAST_DFS.getCode() == storageType){

        }
        return deleter;
    }

}
