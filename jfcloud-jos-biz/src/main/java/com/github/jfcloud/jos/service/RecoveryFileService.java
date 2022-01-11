package com.github.jfcloud.jos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jfcloud.jos.entity.RecoveryFile;
import com.github.jfcloud.jos.vo.RecoveryFileVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lemon
 * @since 2022-01-04
 */
public interface RecoveryFileService extends IService<RecoveryFile> {

    List<RecoveryFileVo> recoveryFileList();

    boolean recoveryFile(Long id);

    boolean deleteFile(Long id);

    boolean deleteFilesBatch(List<Long> ids);

    boolean recoveryFilesBatch(List<Long> ids);
}
