package com.github.jfcloud.jos.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value="ShareFileVo")
@Data
public class ShowShareFileVo {

    @ApiModelProperty(value = "分享文件名")
    private String name;

    @ApiModelProperty(value = "分享文件id")
    private Long shareId;

    @ApiModelProperty(value = "文件关联的元数据id")
    private Long metadataId;

    @ApiModelProperty(value = "文件大小")
    private Double fileSize;

    @ApiModelProperty(value = "分享时间")
    private Date shareTime;

    @ApiModelProperty(value = "结束时间/有效期")
    private Date endTime;

    @ApiModelProperty(value = "分享人名称")
    private String userName;

}
