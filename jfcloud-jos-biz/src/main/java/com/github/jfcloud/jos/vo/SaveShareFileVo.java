package com.github.jfcloud.jos.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="SaveShareFileVo")
@Data
public class SaveShareFileVo {

    @ApiModelProperty(value = "保存的目录id")
    private Long parentId;

    @ApiModelProperty(value = "文件的元数据id")
    private Long metadataId;

    @ApiModelProperty(value = "文件名称")
    private String filename;

    @ApiModelProperty(value = "分享文件的id")
    private Long shareFileId;
}
