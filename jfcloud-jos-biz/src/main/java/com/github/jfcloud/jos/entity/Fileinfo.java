package com.github.jfcloud.jos.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lemon
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jos_fileinfo")
@ApiModel(value="Fileinfo对象", description="")
public class Fileinfo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键Id")
    @TableField(fill = FieldFill.INSERT)
    private Long id;

    @ApiModelProperty(value = "目录名称")
    private String name;

    @ApiModelProperty(value = "权限（777指拥有所有权限）")
    @TableField(fill = FieldFill.INSERT)
    private Integer permissions;

    @ApiModelProperty(value = "父目录Id")
    private Long parentId;

    @ApiModelProperty(value = " 0/1 文件/文件夹")
    private String isFile;

    @ApiModelProperty(value = "文件大小")
    private Double fileSize;

    @ApiModelProperty(value = "用户id")
    private Long fileAuther;

    @ApiModelProperty(value = "关联的元数据Id")
    private Long josMetadataId;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createdDate;

    @ApiModelProperty(value = "修改人")
    @TableField(fill = FieldFill.UPDATE)
    private Long lastModifiedBy;

    @ApiModelProperty(value = "上次修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date lastModifiedDate;

    @ApiModelProperty(value = "删除人")
    private Long deletedBy;

    @ApiModelProperty(value = "删除时间")
    private Date deletedDate;

    @ApiModelProperty(value = "删除标志 0/1 删除/未删除")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deletedFlag;

    @ApiModelProperty(value = "分库id")
    private Long dbId;

    @ApiModelProperty(value = "文件路径")
    private String path;

    /*@ApiModelProperty(value = "恢复文件")
    private RecoveryFile recoveryFile;*/
    
}
