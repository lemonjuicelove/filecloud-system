package com.github.jfcloud.jos.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/*
    目录展示
 */
@ApiModel(value="DirVo")
@Data
public class DirVo {

    @ApiModelProperty(value = "当前目录id")
    private Long id;

    @ApiModelProperty(value = "父目录id")
    private Long parentId;

    @ApiModelProperty(value = "当前目录名称")
    private String name;

}
