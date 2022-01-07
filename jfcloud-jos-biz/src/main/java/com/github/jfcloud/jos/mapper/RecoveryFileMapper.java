package com.github.jfcloud.jos.mapper;

import com.github.jfcloud.jos.entity.RecoveryFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lemon
 * @since 2022-01-04
 */
@Mapper
public interface RecoveryFileMapper extends BaseMapper<RecoveryFile> {

    void selectRecoveryFileList();
}
