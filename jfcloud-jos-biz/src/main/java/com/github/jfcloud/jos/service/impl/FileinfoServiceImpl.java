package com.github.jfcloud.jos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jfcloud.jos.entity.Fileinfo;
import com.github.jfcloud.jos.entity.Metadata;
import com.github.jfcloud.jos.mapper.FileinfoMapper;
import com.github.jfcloud.jos.service.FileinfoService;
import com.github.jfcloud.jos.service.MetadataService;
import com.github.jfcloud.jos.service.RecoveryFileService;
import com.github.jfcloud.jos.util.FileSafeCode;
import com.github.jfcloud.jos.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

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
    private RecoveryFileService recoveryFileService;

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
    public boolean createDir(Long parentId, Fileinfo fileinfo) {

        if (fileinfo == null) return false;

        // 先查询是否存在同名的目录
        String fileName = getRepeatFileName(parentId, fileinfo.getName());
        fileinfo.setName(fileName);

        // 根据parentId查询父目录，获取父目录的路径
        Fileinfo parentFile = this.getById(parentId);
        if (parentFile == null) return false;

        fileinfo.setPath(parentFile.getPath() + "/" + parentFile.getName());
        fileinfo.setParentId(parentId);
        fileinfo.setIsFile("1");

        // 保存
        boolean save = this.save(fileinfo);
        return save;
    }

    // 递归的删除文件或目录
    @Override
    // @Transactional(rollbackFor = Exception.class)
    public boolean removeFile(Fileinfo fileinfo){

        List<Fileinfo> childList = baseMapper.getChildList(fileinfo.getId());

        List<Long> fileIds = new ArrayList<>();
        // List<Long> metadataIds = new ArrayList<>();
        for (Fileinfo fileinfo1 : childList) {
            fileIds.add(fileinfo1.getId());
            /*if ("0".equals(fileinfo1.getIsFile())){ // 要删除的是文件：元数据表中的记录也需要修改
                metadataIds.add(fileinfo1.getJosMetadataId());
            }*/
        }

        // 逻辑删除fileinfo表中的数据
        baseMapper.removeFile(fileIds,19980218L,new Date());

        // 修改元数据表中的状态记录
        // metadataService.updateStatues(metadataIds,19980218L,new Date());

        return true;
    }

    // 上传文件
    /*@Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFile(MultipartFile file, Long parentId, HttpServletRequest request, HttpServletResponse response) {

        Date date = new Date();

        String filename = file.getOriginalFilename();

        // 文件表添加记录
        Fileinfo fileinfo = new Fileinfo();
        Fileinfo parentFile = this.getById(parentId);
        if(null == parentFile){
            return false;
        }

        if (parentFile.getId() != 1){
            fileinfo.setPath(parentFile.getPath() + "/" + parentFile.getName());
        }

        fileinfo.setParentId(parentId);

        // 先查询是否存在同名的文件
        String fileName = getRepeatFileName(parentId, filename);
        fileinfo.setName(fileName);

        fileinfo.setIsFile("0");
        fileinfo.setFileSize((double) file.getSize());

        Metadata metadata = new Metadata();
        if (fileinfo.getFileSize() == 0){ // 说明是空文件：不去元数据表中查，直接创建一个

            File newFile = null;
            try {
                newFile = MultipartFileToFile.uploadFileProcess(file,fileinfo.getName());
            } catch (Exception e) {
                return false;
            }

            // 元数据表添加记录
            metadata.setMd5(FileSafeCode.getMD5(file));
            metadata.setSha1(FileSafeCode.getSha1(newFile));
            metadata.setCrc32(FileSafeCode.getCRC32(newFile));
            metadata.setLocalCtime(date);
            metadata.setPath(fileinfo.getPath());
            metadata.setFileSize(new Double(file.getSize()));
            metadata.setStatus("1");
            metadata.setMimeType(file.getContentType());
            String name = fileinfo.getName();
            name = name.substring(name.lastIndexOf(".")+1);
            metadata.setMimeName(name);
            metadata.setFileStoreKey(newFile.getAbsolutePath());

            metadataService.save(metadata);
        }else{
            // 根据md5码去元数据表中去查有没有重复的文件
            String md5 = FileSafeCode.getMD5(file);
            metadata = metadataService.findMetadataByMd5(md5);
            if (metadata == null){ // 没有重复的文件，再上传

                File newFile = null;
                try {
                    newFile = MultipartFileToFile.uploadFileProcess(file,fileinfo.getName());
                } catch (Exception e) {
                    return false;
                }

                // 元数据表添加记录
                metadata = new Metadata();
                metadata.setMd5(md5);
                metadata.setSha1(FileSafeCode.getSha1(newFile));
                metadata.setCrc32(FileSafeCode.getCRC32(newFile));
                metadata.setLocalCtime(date);
                metadata.setPath(fileinfo.getPath());
                metadata.setFileSize(new Double(file.getSize()));
                metadata.setStatus("1");
                metadata.setMimeType(file.getContentType());
                String name = fileinfo.getName();
                name = name.substring(name.lastIndexOf(".")+1);
                metadata.setMimeName(name);
                metadata.setFileStoreKey(newFile.getAbsolutePath());

                metadataService.save(metadata);
            }
        }
        fileinfo.setJosMetadataId(metadata.getId());

        return this.save(fileinfo);
    }*/

    // 新建一个空文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createContext(Long parentId, Fileinfo fileinfo) {

        if (fileinfo == null) return false;

        // 先查询是否存在同名的文件
        String fileName = getRepeatFileName(parentId, fileinfo.getName());
        fileinfo.setName(fileName);

        // 文件表添加记录
        Fileinfo parentFile = this.getById(parentId);
        if (parentFile == null) return false;

        fileinfo.setPath(parentFile.getPath() + "/" + parentFile.getName());
        fileinfo.setParentId(parentId);
        fileinfo.setIsFile("0");

        // 元数据表添加记录
        Metadata metadata = new Metadata();
        metadata.setLocalCtime(new Date());
        metadata.setPath(fileinfo.getPath());
        metadata.setMd5(UploadUtil.EMPTYMD5);
        metadata.setSha1(UploadUtil.EMPTYSHA1);
        metadata.setCrc32(UploadUtil.EMPTYCRC32);
        metadata.setStatus("1");
        metadata.setMimeType(UploadUtil.getMine(fileinfo.getName()));
        metadata.setMimeName(UploadUtil.getPro(fileinfo.getName()));
        metadata.setFileStoreKey(UploadUtil.EMPTYFILEPATH);

        metadataService.save(metadata);
        fileinfo.setJosMetadataId(metadata.getId());
        this.save(fileinfo);

        return true;
    }

    // 根据id修改文件名
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFile(Long id, String name) {

        Fileinfo fileinfo = this.getById(id);

        if (fileinfo == null) return false;
        // 先查询是否存在同名的文件或目录
        Long parentId = fileinfo.getParentId();
        String fileName = getRepeatFileName(parentId, name);
        fileinfo.setName(fileName);

        boolean update = updateById(fileinfo);

        // 要修改所有子目录的文件路径
        if ("1".equals(fileinfo.getIsFile())){
            updateFilePath(fileinfo);
        }

        return update;
    }

    // 递归的修改文件路径
    private void updateFilePath(Fileinfo fileinfo){

        if (fileinfo == null) return;

        QueryWrapper<Fileinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",fileinfo.getId());
        List<Fileinfo> fileinfos = this.list(queryWrapper);

        for (Fileinfo fileinfo1 : fileinfos) {
            fileinfo1.setPath(fileinfo.getPath()+"/"+fileinfo.getName());
            if ("1".equals(fileinfo1.getIsFile())){ // 是目录：递归的修改子集
                updateFilePath(fileinfo1);
            }
            // 修改自己
            updateById(fileinfo1);
            // 修改元数据表中的path
            Metadata metadata = metadataService.getById(fileinfo1.getJosMetadataId());
            if (metadata != null){
                metadata.setPath(fileinfo1.getPath());
                metadataService.updateById(metadata);
            }
        }
    }

    // 根据文件id下载文件
    @Override
    public Map<String,String> downloadFile(Long id) {

        // 获取文件信息
        Fileinfo fileinfo = this.getById(id);

        if (fileinfo == null || "1".equals(fileinfo.getIsFile())){
            return null;
        }

        // 获取元数据id，去元数据表找到文件进行下载
        Long metadataId = fileinfo.getJosMetadataId();
        Metadata metadata = metadataService.getById(metadataId);

        Map<String,String> res = new HashMap<>();
        res.put("name",fileinfo.getName());
        res.put("path",metadata.getFileStoreKey());
        return res;
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
    public boolean moveFile(Long sourceId, Long targetId) {

        Fileinfo source = this.getById(sourceId);
        Fileinfo target = this.getById(targetId);

        if (source == null || target == null) return false;

        if (target.getPath().startsWith(source.getPath()) && target.getPath().length() > source.getPath().length()) return false;

        // 判断是否有同名的文件
        source.setName(getRepeatFileName(targetId,source.getName()));

        // 更改parentId
        source.setParentId(targetId);

        // 更改path
        source.setPath(target.getPath()+"/"+target.getName());

        this.updateById(source);

        // 递归修改子文件的path
        updateFilePath(source);

        return true;
    }

}
