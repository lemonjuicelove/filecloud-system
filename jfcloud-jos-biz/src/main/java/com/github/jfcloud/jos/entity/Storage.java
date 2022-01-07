package com.github.jfcloud.jos.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 存储方式表
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jos_storage")
@ApiModel(value="Storage对象", description="存储方式表")
public class Storage implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "存储方式id")
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "存储方式名")
    private String name;

    @ApiModelProperty(value = "可用标志 0/1 可用/不可用")
    private String enable;

    @ApiModelProperty(value = "排序数字")
    private Integer orderNum;

    @ApiModelProperty(value = "存储方式类型id")
    private Long josStorageTypeId;

    @ApiModelProperty(value = "权限")
    private Long tenantId;

    @ApiModelProperty(value = "创建人")
    private Long createdBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createdDate;

    @ApiModelProperty(value = "修改人")
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
