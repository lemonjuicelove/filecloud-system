package com.github.jfcloud.jos.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value="RecoveryFileVo")
@Data
public class RecoveryFileVo {

    @ApiModelProperty(value = "回收文件id")
    private Long recoveryFileId;

    @ApiModelProperty(value = "文件记录id")
    private Long fileinfoId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @ApiModelProperty(value = " 0/1 文件/文件夹")
    private String isFile;

    @ApiModelProperty(value = "创建时间")
    private Date createdDate;


    @ApiModelProperty(value = "删除时间")
    private Date deletedDate;

}
