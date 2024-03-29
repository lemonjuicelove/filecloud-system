package com.github.jfcloud.jos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jfcloud.jos.core.common.StorageTypeEnum;
import com.github.jfcloud.jos.core.factory.FileOperatorFactory;
import com.github.jfcloud.jos.core.operation.download.entity.DownloadFile;
import com.github.jfcloud.jos.core.util.FileUtil;
import com.github.jfcloud.jos.entity.Fileinfo;
import com.github.jfcloud.jos.entity.Metadata;
import com.github.jfcloud.jos.exception.BizException;
import com.github.jfcloud.jos.mapper.FileinfoMapper;
import com.github.jfcloud.jos.service.FileinfoService;
import com.github.jfcloud.jos.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Service
public class FileinfoServiceImpl extends ServiceImpl<FileinfoMapper, Fileinfo> implements FileinfoService {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private FileOperatorFactory fileOperatorFactory;

    // 根据parentId查询当前目录下的文件
    @Override
    public List<Fileinfo> findFileByParentId(Long parentId, String orderBy) {

        QueryWrapper<Fileinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentId);

        if ("time".equals(orderBy)){ // 按创建时间排序
            queryWrapper.orderByAsc("created_date");
        }else if ("size".equals(orderBy)){ // 按文件大小排序
            queryWrapper.orderByAsc("file_size");
        }else if ("name".equals(orderBy)){ // 按文件名称排序
            queryWrapper.orderByAsc("name");
        }

        List<Fileinfo> fileinfos = this.list(queryWrapper);

        // 文件夹显示在前，文件显示在后
        List<Fileinfo> fileDir = new LinkedList<>();
        List<Fileinfo> fileContext = new LinkedList<>();
        for (Fileinfo fileinfo : fileinfos) {
            if ("0".equals(fileinfo.getIsFile())){
                fileContext.add(fileinfo);
            }else{
                fileDir.add(fileinfo);
            }
        }

        List<Fileinfo> list = new ArrayList<>();
        for (Fileinfo fileinfo : fileDir) {
            list.add(fileinfo);
        }
        for (Fileinfo fileinfo : fileContext) {
            list.add(fileinfo);
        }

        return list;
    }

    // 新建一个目录
    @Override
    public boolean createDir(Long parentId, Long userId, String filename) {

        if (StringUtils.isEmpty(filename)) return false;

        // 根据parentId查询父目录，获取父目录的路径
        Fileinfo parentFile = this.getById(parentId);
        if (parentFile == null) return false;

        Fileinfo fileinfo = new Fileinfo();

        // 先查询是否存在同名的目录
        String fileName = getRepeatFileName(parentId, fileinfo.getName());
        fileinfo.setName(fileName);

        fileinfo.setPath(parentFile.getPath() + "/" + parentFile.getName());
        fileinfo.setParentId(parentId);
        fileinfo.setIsFile("1");
        fileinfo.setFileAuther(userId);
        fileinfo.setCreatedBy(userId);

        // 保存
        return this.save(fileinfo);
    }

    // 递归的删除文件或目录
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFile(Fileinfo fileinfo,Long userId){

        if (fileinfo == null) return false;

        List<Fileinfo> childList = baseMapper.getChildList(fileinfo.getId());

        List<Long> fileIds = new ArrayList<>();
        List<Long> metadataIds = new ArrayList<>();
        for (Fileinfo fileinfo1 : childList) {
            fileIds.add(fileinfo1.getId());
            if ("0".equals(fileinfo1.getIsFile())){ // 要删除的是文件：元数据表中的记录也需要删除
                metadataIds.add(fileinfo1.getJosMetadataId());
            }
        }

        // 逻辑删除fileinfo表中的数据
        baseMapper.removeFile(fileIds,userId,new Date());

        // 逻辑删除元数据表中的数据
        metadataService.removeMetadataByIds(metadataIds,userId);

        return true;
    }

    // 新建一个空文件：直接关联本地存储的空文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createContext(Long parentId, Long userId, String filename) {

        if (StringUtils.isEmpty(filename)) return false;
        Fileinfo parentFile = this.getById(parentId);
        if (parentFile == null) return false;

        Fileinfo fileinfo = new Fileinfo();

        // 先查询是否存在同名的文件
        String fileName = getRepeatFileName(parentId, fileinfo.getName());
        fileinfo.setName(fileName);

        // 文件表添加记录
        fileinfo.setPath(parentFile.getPath() + "/" + parentFile.getName());
        fileinfo.setParentId(parentId);
        fileinfo.setIsFile("0");
        fileinfo.setFileAuther(userId);
        fileinfo.setCreatedBy(userId);

        // 元数据表添加记录
        Metadata metadata = new Metadata();
        metadata.setLocalCtime(new Date());
        metadata.setPath(fileinfo.getPath());
        metadata.setMd5(fileOperatorFactory.getLocalConfig().getEmptyMD5());
        metadata.setMimeType(FileUtil.getMine(fileinfo.getName()));
        metadata.setMimeName(FileUtil.getPro(fileinfo.getName()));
        metadata.setJosStorageId(StorageTypeEnum.LOCAL.getCode());
        metadata.setFileStoreKey(fileOperatorFactory.getLocalConfig().getEmptyFilePath());
        metadata.setCreatedBy(userId);

        boolean save1 = metadataService.save(metadata);
        fileinfo.setJosMetadataId(metadata.getId());
        boolean save2 = this.save(fileinfo);

        if (save1 && save2){
            return true;
        }else{
            throw new BizException("新建文件失败");
        }

    }

    // 根据id修改文件名
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFile(Long id, String name,Long userId) {

        Fileinfo fileinfo = this.getById(id);

        if (fileinfo == null) return false;

        // 先查询是否存在同名的文件或目录
        Long parentId = fileinfo.getParentId();
        String fileName = getRepeatFileName(parentId, name);
        fileinfo.setName(fileName);
        fileinfo.setLastModifiedBy(userId);

        updateById(fileinfo);

        // 要修改所有子目录的文件路径
        if ("1".equals(fileinfo.getIsFile())){
            updateFilePath(fileinfo);
        }

        return true;
    }

    // 递归的修改文件路径
    private void updateFilePath(Fileinfo fileinfo){

        if (fileinfo == null) return;

        QueryWrapper<Fileinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",fileinfo.getId());
        List<Fileinfo> fileinfos = this.list(queryWrapper);

        for (Fileinfo fileinfo1 : fileinfos) {
            fileinfo1.setPath(fileinfo.getPath()+"/"+fileinfo.getName());
            fileinfo1.setLastModifiedBy(fileinfo.getLastModifiedBy());
            if ("1".equals(fileinfo1.getIsFile())){ // 是目录：递归的修改子集
                updateFilePath(fileinfo1);
            }else{ // 是文件：修改元数据表中的path
                Metadata metadata = metadataService.getById(fileinfo1.getJosMetadataId());
                if (metadata != null){
                    metadata.setPath(fileinfo1.getPath());
                    metadata.setLastModifiedBy(fileinfo.getLastModifiedBy());
                    metadataService.updateById(metadata);
                }
            }

            // 修改自己
            updateById(fileinfo1);
        }
    }

    // 根据文件id下载文件
    @Override
    public DownloadFile downloadFile(Long id) {

        // 获取文件信息
        Fileinfo fileinfo = this.getById(id);

        if (fileinfo == null || "1".equals(fileinfo.getIsFile())){
            return null;
        }

        // 获取元数据id，去元数据表找到文件进行下载
        Long metadataId = fileinfo.getJosMetadataId();
        Metadata metadata = metadataService.getById(metadataId);

        if (metadata == null) return null;

        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFilename(fileinfo.getName());
        downloadFile.setPath(metadata.getFileStoreKey());
        downloadFile.setMetadata(String.valueOf(metadata.getId()));
        if (metadata.getJosStorageId() == StorageTypeEnum.LOCAL.getCode()){
            downloadFile.setStorageType(StorageTypeEnum.LOCAL);
        }else if (metadata.getJosStorageId() == StorageTypeEnum.ALIYUN_OSS.getCode()){
            downloadFile.setStorageType(StorageTypeEnum.ALIYUN_OSS);
        }else if (metadata.getJosStorageId() == StorageTypeEnum.FAST_DFS.getCode()){
            downloadFile.setStorageType(StorageTypeEnum.FAST_DFS);
        }

        return downloadFile;
    }

    // 批量下载文件
    @Override
    public List<DownloadFile> downloadFilesBatch(List<Long> ids) {

        List<DownloadFile> downloadFiles = new ArrayList<>();
        for (Long id : ids) {
            DownloadFile downloadFile = downloadFile(id);
            if (downloadFile != null) downloadFiles.add(downloadFile);
        }

        return downloadFiles;
    }

    /*
        获取重复文件名
        场景1: 文件还原时，在 savefilePath 路径下，保存 测试.txt 文件重名，则会生成 测试(1).txt
        场景2： 上传文件时，在 savefilePath 路径下，保存 测试.txt 文件重名，则会生成 测试(1).txt
     */
    @Override
    public String getRepeatFileName(Long parentId, String fileName){

        QueryWrapper<Fileinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentId);
        queryWrapper.eq("name",fileName);
        Fileinfo one = this.getOne(queryWrapper);
        if (one == null){
            return fileName;
        }
        int i = 0;
        if (fileName.lastIndexOf(".") != -1){ // 是文件
            String pre = fileName.substring(0,fileName.lastIndexOf("."));
            String pro = fileName.substring(fileName.lastIndexOf(".")+1);
            while(one != null){
                i++;
                QueryWrapper<Fileinfo> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("parent_id",parentId);
                queryWrapper1.eq("name",pre+"("+i+")."+pro);
                one = this.getOne(queryWrapper1);
            }
            return pre+"("+i+")."+pro;
        }else{ // 是目录
            while(one != null){
                i++;
                QueryWrapper<Fileinfo> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("parent_id",parentId);
                queryWrapper1.eq("name",fileName+"("+i+")");
                one = this.getOne(queryWrapper1);
            }
            return fileName+"("+i+")";
        }
    }

    // 根据ids查询被删除的数据
    @Override
    public List<Fileinfo> listDeletedByIds(List<Long> ids) {
        List<Fileinfo> deletedFileinfoList = baseMapper.listDeletedByIds(ids);
        return deletedFileinfoList;
    }

    // 根据id查询被逻辑删除的所有数据
    @Override
    public List<Fileinfo> getDeletedChildList(Long id) {
        List<Fileinfo> deletedFileinfoList = baseMapper.getDeletedChildList(id);
        return deletedFileinfoList;
    }

    // 根据ids恢复文件
    @Override
    public void recoveryFile(List<Long> ids) {
        // 恢复fileinfo表中的记录
        baseMapper.recoveryFile(ids);
    }

    // 根据ids彻底删除文件
    @Override
    public void deleteFiles(List<Long> ids) {
        baseMapper.deleteFiles(ids);
    }

    // 移动文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveFile(Long sourceId, Long targetId,Long userId) {

        Fileinfo source = this.getById(sourceId);
        Fileinfo target = this.getById(targetId);

        if (source == null || target == null) throw new BizException("文件移动失败");

        if (source.equals(target)) throw new BizException("文件移动失败");

        // 避免高目录移动到低目录
        if (target.getPath().startsWith(source.getPath()) && target.getPath().length() > source.getPath().length()) throw new BizException("文件移动失败");

        // 避免原地移动
        if (source.getParentId().equals(target.getId())) throw new BizException("文件移动失败");

        // 判断是否有同名的文件
        source.setName(getRepeatFileName(targetId,source.getName()));

        // 更改parentId
        source.setParentId(targetId);

        // 更改path
        source.setPath(target.getPath()+"/"+target.getName());

        source.setLastModifiedBy(userId);

        this.updateById(source);

        // 更改元数据表
        if("0".equals(source.getIsFile())){
            Metadata metadata = metadataService.getById(source.getJosMetadataId());
            if (metadata != null){
                metadata.setPath(source.getPath());
                metadata.setLastModifiedBy(userId);
                metadataService.updateById(metadata);
            }
        }

        // 递归修改子文件的path
        updateFilePath(source);
    }

}
