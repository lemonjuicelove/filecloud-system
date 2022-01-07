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
 * 存储方式参数表
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jos_storage_type_param")
@ApiModel(value="StorageTypeParam对象", description="存储方式参数表")
public class StorageTypeParam implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "存储方式对应id")
    private Long josStorageTypeId;

    @ApiModelProperty(value = "字段名称")
    private String paramName;

    @ApiModelProperty(value = "字段key值")
    private String paramValue;

    @ApiModelProperty(value = "实例数据")
    private String paramExample;

    @ApiModelProperty(value = "是否为空  0/1 是/否")
    private String isBlank;

    @ApiModelProperty(value = "正则判断")
    private String regularValue;

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
