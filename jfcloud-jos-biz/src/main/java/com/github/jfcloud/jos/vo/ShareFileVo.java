package com.github.jfcloud.jos.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="ShareFileVo")
@Data
public class ShareFileVo {

    @ApiModelProperty(value = "文件的id")
    private Long id;

    @ApiModelProperty(value = "最大访问人数")
    private Long maxCount;

    @ApiModelProperty(value = "有效期")
    private Integer time;

    @ApiModelProperty(value = "分享人id")
    private Long userId;

    @ApiModelProperty(value = "分享人名称")
    private String name;

}
