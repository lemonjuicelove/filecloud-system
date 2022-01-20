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

    void recoveryFile(Long id);

    void deleteFile(Long id);

    void deleteFilesBatch(List<Long> ids);

    void recoveryFilesBatch(List<Long> ids);
}
