package com.github.jfcloud.jos.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author lemon
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jos_recovery_file")
@ApiModel(value="RecoveryFile对象", description="")
public class RecoveryFile implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键Id")
    @TableField(fill = FieldFill.INSERT)
    private Long id;

    @ApiModelProperty(value = "文件目录id")
    private Long fileinfoId;

    @ApiModelProperty(value = "删除人")
    private Long deletedBy;

    @ApiModelProperty(value = "删除时间")
    private Date deletedDate;


}
