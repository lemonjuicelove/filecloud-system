package com.github.jfcloud.jos.core.operation.upload.entity;

import com.aliyun.oss.model.PartETag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFile {

    private int chunkNumber; // 当前切片
    private long chunkSize; // 切片大小
    private int totalChunks; // 切片总数
    private String identifier; // 当前切片的md5
    private String wholeIdentifier; // 总文件的md5
    private String fileName; // 文件名称
    private MultipartFile file; // 切片文件
    private String uploadId; // oss分片上传的唯一标识
    private List<PartETag> partETags; // 分片集合

}
