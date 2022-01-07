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
 * <p>
 * 元数据表
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jos_metadata")
@ApiModel(value="Metadata对象", description="元数据表")
public class Metadata implements Serializable {

    private static final long serialVersionUID=1L;

    @TableField(fill = FieldFill.INSERT)
    private Long id;

    @ApiModelProperty(value = "md5校验码")
    private String md5;

    @ApiModelProperty(value = "sha1校验码")
    private String sha1;

    @ApiModelProperty(value = "crc32校验码")
    private String crc32;

    @ApiModelProperty(value = "存储路径")
    private String path;

    @ApiModelProperty(value = "文件大小")
    private Double fileSize;

    @ApiModelProperty(value = "状态 (0/1  删除/正常  )")
    @TableField(fill = FieldFill.INSERT)
    private String status;

    @ApiModelProperty(value = "媒体类型")
    private String mimeType;

    @ApiModelProperty(value = "后缀名")
    private String mimeName;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "上传时间")
    private Date localCtime;

    @ApiModelProperty(value = "存储方式Id")
    private Long josStorageId;

    @ApiModelProperty(value = "存储文件地址唯一标识")
    private String fileStoreKey;

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

}
