package com.github.jfcloud.jos.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipartFileParam {

    @ApiModelProperty("当前切片")
    private int chunkNumber;

    @ApiModelProperty("切片大小")
    private long chunkSize;

    @ApiModelProperty("切片总数")
    private int totalChunks;

    @ApiModelProperty("当前切片的md5")
    private String identifier;

    @ApiModelProperty("总文件的md5")
    private String wholeIdentifier;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("切片文件")
    private MultipartFile file;


}
