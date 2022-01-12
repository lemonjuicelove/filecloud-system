package com.github.jfcloud.jos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jfcloud.jos.entity.Fileinfo;
import com.github.jfcloud.jos.entity.RecoveryFile;
import com.github.jfcloud.jos.mapper.RecoveryFileMapper;
import com.github.jfcloud.jos.service.FileinfoService;
import com.github.jfcloud.jos.service.RecoveryFileService;
import com.github.jfcloud.jos.vo.RecoveryFileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lemon
 * @since 2022-01-04
 */
@Service
public class RecoveryFileServiceImpl extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements RecoveryFileService {

    @Autowired
    private FileinfoService fileinfoService;

    // 获取回收站的文件列表
    @Override
    public List<RecoveryFileVo> recoveryFileList() {

        QueryWrapper<RecoveryFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("deleted_date");

        // recovery_file表中的数据
        List<RecoveryFile> recoveryFiles = this.list(queryWrapper);

        if (recoveryFiles.size() == 0) return new ArrayList<>();

        List<Long> ids = new ArrayList<>();
        for (RecoveryFile recoveryFile : recoveryFiles) {
            ids.add(recoveryFile.getFileinfoId());
        }

        // fileinfo表中和recovery_file表关联的数据
        List<Fileinfo> deletedFileinfoList = fileinfoService.listDeletedByIds(ids);

        List<RecoveryFileVo> recoveryFileVos = new ArrayList<>();

        for (RecoveryFile recoveryFile : recoveryFiles) {
            for (Fileinfo fileinfo : deletedFileinfoList) {
                if (recoveryFile.getFileinfoId().equals(fileinfo.getId())){
                    RecoveryFileVo vo = new RecoveryFileVo();
                    vo.setRecoveryFileId(recoveryFile.getId());
                    vo.setFileinfoId(fileinfo.getId());
                    vo.setIsFile(fileinfo.getIsFile());
                    vo.setFileName(fileinfo.getName());
                    vo.setFilePath(fileinfo.getPath());
                    vo.setCreatedDate(fileinfo.getCreatedDate());
                    vo.setDeletedDate(recoveryFile.getDeletedDate());
                    recoveryFileVos.add(vo);
                }
            }
        }

        return recoveryFileVos;
    }

    // 恢复文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recoveryFile(Long id) {

        RecoveryFile recoveryFile = this.getById(id);
        if (recoveryFile == null){
            return true;
        }
        // 删除recoveryFile表中的记录
        this.removeById(id);

        // 将fileinfo表中被删除的文件信息恢复
        List<Fileinfo> deletedChildList = fileinfoService.getDeletedChildList(recoveryFile.getFileinfoId());
        List<Long> ids = new ArrayList<>();
        for (Fileinfo fileinfo : deletedChildList) {
            ids.add(fileinfo.getId());
        }

        fileinfoService.recoveryFile(ids);

        return true;
    }

    // 彻底删除文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long id) {
        RecoveryFile recoveryFile = this.getById(id);
        if (recoveryFile == null) return false;

        // 删除recoveryFile表中的记录
        this.removeById(id);

        // 彻底删除fileinfo表中的记录
        List<Fileinfo> deletedChildList = fileinfoService.getDeletedChildList(recoveryFile.getFileinfoId());
        List<Long> ids = new ArrayList<>();
        for (Fileinfo fileinfo : deletedChildList) {
            ids.add(fileinfo.getId());
        }

        fileinfoService.deleteFiles(ids);

        return true;
    }

    // 批量删除
    @Override
    public boolean deleteFilesBatch(List<Long> ids) {
        for (Long id : ids) {
            deleteFile(id);
        }
        return true;
    }

    // 批量恢复
    @Override
    public boolean recoveryFilesBatch(List<Long> ids) {
        for (Long id : ids) {
            recoveryFile(id);
        }
        return true;
    }

}
