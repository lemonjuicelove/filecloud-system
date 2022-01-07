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
 * 存储方式类型表
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jos_storage_type")
@ApiModel(value="StorageType对象", description="存储方式类型表")
public class StorageType implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "存储类型的key")
    private String key;

    @ApiModelProperty(value = "存储类型的value")
    private String value;

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
