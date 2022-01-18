package com.github.jfcloud.jos.core.operation.upload.entity;

import com.aliyun.oss.model.PartETag;
import com.github.jfcloud.jos.core.common.StorageTypeEnum;
import com.github.jfcloud.jos.core.common.UploadFileStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class UploadFileResults {

    private String fileName; // 文件名称
    private long fileSize; // 文件大小
    private String fileUrl; // 文件路径
    private StorageTypeEnum storageType; // 存储类型
    private UploadFileStatusEnum status; // 上传文件状态
    private String uploadId; // 分片上传事件唯一id
    private List<PartETag> partETags; // 分片列表

}
