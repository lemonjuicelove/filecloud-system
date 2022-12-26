package com.github.jfcloud.jos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.jfcloud.jos.core.common.UploadFileStatusEnum;
import com.github.jfcloud.jos.core.exception.operation.DownloadException;
import com.github.jfcloud.jos.core.factory.FileOperatorFactory;
import com.github.jfcloud.jos.core.operation.download.Downloader;
import com.github.jfcloud.jos.core.operation.download.entity.DownloadFile;
import com.github.jfcloud.jos.core.operation.upload.Uploader;
import com.github.jfcloud.jos.core.operation.upload.entity.UploadFile;
import com.github.jfcloud.jos.core.operation.upload.entity.UploadFileResults;
import com.github.jfcloud.jos.core.util.DateUtil;
import com.github.jfcloud.jos.core.util.FileUtil;
import com.github.jfcloud.jos.entity.Fileinfo;
import com.github.jfcloud.jos.entity.Metadata;
import com.github.jfcloud.jos.entity.RecoveryFile;
import com.github.jfcloud.jos.exception.BizException;
import com.github.jfcloud.jos.service.FileinfoService;
import com.github.jfcloud.jos.service.MetadataService;
import com.github.jfcloud.jos.service.RecoveryFileService;
import com.github.jfcloud.jos.util.CommonResult;
import com.github.jfcloud.jos.util.RedisUtil;
import com.github.jfcloud.jos.vo.DirVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Api("文件管理")
@RestController
@RequestMapping("/jos/fileinfo")
public class FileinfoController {

    @Autowired
    private FileinfoService fileinfoService;

    @Autowired
    private RecoveryFileService recoveryFileService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FileOperatorFactory fileOperatorFactory;

    @ApiOperation("上传文件")
    @PostMapping("/uploadFile/{parentId}/{userId}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult uploadFile(UploadFile uploadFile,
                                   @PathVariable("parentId") Long parentId,
                                   @PathVariable("userId") Long userId){

        if (uploadFile == null) return CommonResult.error().message("上传文件失败");

        Fileinfo parentFileinfo = fileinfoService.getById(parentId);
        if (parentFileinfo == null) return CommonResult.error().message("上传文件失败");

        // 去元数据表中查询整个文件的md码，如果存在，秒传
        String wholeIdentifier = uploadFile.getWholeIdentifier();
        Metadata metadata = metadataService.findMetadataByMd5(wholeIdentifier);

        if (metadata != null) { // 元数据存在

            // fileinfo表中添加记录
            Fileinfo fileinfo = new Fileinfo();
            fileinfo.setPath(parentFileinfo.getPath() + "/" + parentFileinfo.getName());
            fileinfo.setParentId(parentId);
            fileinfo.setName(fileinfoService.getRepeatFileName(parentId,uploadFile.getFileName()));
            fileinfo.setIsFile("0");
            fileinfo.setFileSize(metadata.getFileSize());
            fileinfo.setFileAuther(userId);

            // 元数据表中添加记录
            Metadata newMetadata = new Metadata();
            newMetadata.setMd5(wholeIdentifier);
            newMetadata.setPath(fileinfo.getPath());
            newMetadata.setFileSize(metadata.getFileSize());
            newMetadata.setMimeType(FileUtil.getMine(uploadFile.getFileName()));
            newMetadata.setMimeName(FileUtil.getPro(uploadFile.getFileName()));
            newMetadata.setLocalCtime(new Date());
            newMetadata.setJosStorageId(metadata.getJosStorageId());
            newMetadata.setFileStoreKey(metadata.getFileStoreKey());
            newMetadata.setCreatedBy(userId);

            metadataService.save(newMetadata);
            fileinfo.setJosMetadataId(newMetadata.getId());
            fileinfoService.save(fileinfo);

            UploadFileResults result = new UploadFileResults();
            BeanUtils.copyProperties(uploadFile,result);
            result.setStatus(UploadFileStatusEnum.SUCCESS);
            return CommonResult.ok().data("result",result);
        }

        // 元数据不存在：去缓存中查询是第几块分片
        String pre = FileUtil.getPre(uploadFile.getFileName());
        String key = pre + "-" + wholeIdentifier;
        String value = uploadFile.getChunkNumber() + "-" + uploadFile.getIdentifier();
        if (redisUtil.hasKey(key)){
            List<Object> values = redisUtil.getList(key);
            for (Object o : values) {
                if (o.equals(value)){ // 该分片存在
                    UploadFileResults result = new UploadFileResults();
                    BeanUtils.copyProperties(uploadFile,result);
                    result.setStatus(UploadFileStatusEnum.UNCOMPLATE);
                    return CommonResult.ok().data("result",result);
                }
            }
        }

        // 分片不存在 上传分片
        Uploader uploader = fileOperatorFactory.getUploader();
        UploadFileResults uploadResults = uploader.upload(uploadFile);

        if (uploadResults.getStatus() == UploadFileStatusEnum.UNCOMPLATE){ // 分片文件上传成功
            redisUtil.setList(key,value,60*60*24);
            return CommonResult.ok().data("result",uploadResults);
        }else if (uploadResults.getStatus() == UploadFileStatusEnum.FAIL){ // 分片文件上传失败
            return CommonResult.error().data("result",uploadResults).message("文件上传失败");
        }

        redisUtil.del(key);

        // fileinfo表中添加记录
        Fileinfo fileinfo = new Fileinfo();
        fileinfo.setPath(parentFileinfo.getPath() + "/" + parentFileinfo.getName());
        fileinfo.setParentId(parentId);
        fileinfo.setName(fileinfoService.getRepeatFileName(parentId,uploadFile.getFileName()));
        fileinfo.setIsFile("0");
        fileinfo.setFileSize(uploadResults.getFileSize());
        fileinfo.setFileAuther(userId);

        // 元数据表中添加记录
        Metadata newMetadata = new Metadata();
        newMetadata.setMd5(wholeIdentifier);
        newMetadata.setPath(fileinfo.getPath());
        newMetadata.setFileSize(uploadResults.getFileSize());
        newMetadata.setMimeType(FileUtil.getMine(uploadFile.getFileName()));
        newMetadata.setMimeName(FileUtil.getPro(uploadFile.getFileName()));
        newMetadata.setLocalCtime(new Date());
        newMetadata.setJosStorageId(uploadResults.getStorageType().getCode());
        newMetadata.setFileStoreKey(uploadResults.getFileUrl());
        newMetadata.setCreatedBy(userId);

        metadataService.save(newMetadata);
        fileinfo.setJosMetadataId(newMetadata.getId());
        fileinfoService.save(fileinfo);

        return CommonResult.ok().data("result",uploadResults);
    }


    @ApiOperation("根据id查询文件信息")
    @GetMapping("/findFileById/{id}")
    public CommonResult findFileById(@PathVariable("id") Long id){
        Fileinfo fileinfo = fileinfoService.getById(id);
        return CommonResult.ok().data("文件信息",fileinfo);
    }


    @ApiOperation("根据parentId查询当前目录下的文件,并排序")
    @GetMapping("/findFileByParentId/{parentId}/{orderBy}")
    public CommonResult findFileByParentId(@PathVariable("parentId") Long parentId,
                                           @PathVariable("orderBy") String orderBy){

        if (StringUtils.isEmpty(orderBy)){
            orderBy = "time";
        }else if (!"time".equals(orderBy) && !"size".equals(orderBy) && !"name".equals(orderBy)){
            throw new BizException("排序名称错误");
        }

        List<Fileinfo> fileinfos = fileinfoService.findFileByParentId(parentId,orderBy);
        return CommonResult.ok().data("子文件",fileinfos);
    }


    @ApiOperation("新建一个空文件")
    @PostMapping("/createContext/{parentId}/{userId}/{filename}")
    public CommonResult createContext(@PathVariable("parentId") Long parentId,
                                      @PathVariable("userId") Long userId,
                                      @PathVariable("filename") String filename){
        boolean save = fileinfoService.createContext(parentId,userId,filename);
        return save ? CommonResult.ok() : CommonResult.error().message("新建失败");
    }


    @ApiOperation("新建一个目录")
    @PostMapping("/createDir/{parentId}/{userId}/{filename}")
    public CommonResult createDir(@PathVariable("parentId") Long parentId,
                                  @PathVariable("userId") Long userId,
                                  @PathVariable("filename") String filename){
        boolean save = fileinfoService.createDir(parentId,userId,filename);
        return save ? CommonResult.ok() : CommonResult.error().message("新建失败");
    }


    @ApiOperation("根据文件名查询文件")
    @GetMapping("/findFileByName/{name}")
    public CommonResult findFileByName(@PathVariable("name") String name){

        QueryWrapper<Fileinfo> qw = new QueryWrapper<>();
        qw.eq("name",name);
        List<Fileinfo> fileinfoList = fileinfoService.list(qw);
        
        return CommonResult.ok().data("文件信息",fileinfoList);
    }


    @ApiOperation("根据id删除文件")
    @DeleteMapping("/deleteFile/{id}/{userId}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult deleteFile(@PathVariable("id") Long id,
                                   @PathVariable("userId") Long userId){
        Fileinfo fileinfo = fileinfoService.getById(id);
        if (null == fileinfo){
            return CommonResult.error().message("文件不存在");
        }else{
            boolean remove = fileinfoService.removeFile(fileinfo,userId);
            // 在回收站表中添加一条记录
            RecoveryFile recoveryFile = new RecoveryFile();
            recoveryFile.setFileinfoId(fileinfo.getId());
            recoveryFile.setDeletedBy(userId);
            recoveryFile.setDeletedDate(new Date());
            boolean save = recoveryFileService.save(recoveryFile);

            if (remove && save){
                return CommonResult.ok();
            }else{
                throw new BizException("删除文件失败");
            }
        }
    }

    @ApiOperation("根据id批量删除文件")
    @DeleteMapping("/deleteFilesBatch/{userId}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult deleteFilesBatch(@PathVariable("userId") Long userId,
                                         @RequestBody List<Long> ids){

        for (Long id : ids) {
            Fileinfo fileinfo = fileinfoService.getById(id);
            if (fileinfo != null){
                boolean remove = fileinfoService.removeFile(fileinfo,userId);
                // 在回收站表中添加一条记录
                RecoveryFile recoveryFile = new RecoveryFile();
                recoveryFile.setFileinfoId(fileinfo.getId());
                recoveryFile.setDeletedBy(userId);
                recoveryFile.setDeletedDate(new Date());
                boolean save = recoveryFileService.save(recoveryFile);

                if (!remove || !save) throw new BizException("删除文件失败");
            }
        }

        return CommonResult.ok();
    }


    @ApiOperation("根据id修改文件名")
    @PostMapping("/updateFile/{id}/{name}/{userId}")
    public CommonResult updateFile(@PathVariable("id") Long id,
                                   @PathVariable("name") String name,
                                   @PathVariable("userId") Long userId){

        if (StringUtils.isEmpty(name)) return CommonResult.error().message("文件名不能为空");

        boolean update = fileinfoService.updateFile(id, name,userId);

        return update ? CommonResult.ok() : CommonResult.error().message("修改失败");
    }


    @ApiOperation("根据id下载文件")
    @GetMapping("/downloadFile/{id}")
    public void downloadFile(@PathVariable("id") Long id,
                             HttpServletRequest request,
                             HttpServletResponse response){

        // 获取文件的下载路径和文件名
        DownloadFile downloadFile = fileinfoService.downloadFile(id);
        if (downloadFile == null) throw new DownloadException("下载文件不存在");

        // 下载文件
        Downloader downloader = fileOperatorFactory.getDownloader(downloadFile.getStorageType().getCode());
        downloader.download(downloadFile,request,response);
    }


    @ApiOperation("批量下载文件")
    @PostMapping("/downloadFilesBatch/{id}")
    public void downloadFilesBatch(@RequestBody List<Long> ids,
                             HttpServletRequest request,
                             HttpServletResponse response){

        List<DownloadFile> downloadFiles = fileinfoService.downloadFilesBatch(ids);
        if (downloadFiles.size() == 0) throw new DownloadException("下载文件不存在");;

        response.reset();
        // 设置响应的编码方式
        response.setCharacterEncoding("utf-8");
        // 设置响应的内容类型
        response.setContentType("PPLICATION/OCTET-STREAM");

        // 设置压缩包的名字
        String dates = DateUtil.formatDate(new Date());
        String billname = "附件包-" + dates;
        String downloadName = billname + ".zip";

        // 设置返回客户端浏览器，解决文件名乱码问题
        String agent = request.getHeader("USER-AGENT");
        try{
            // 针对以ie为内核的浏览器
            if (agent.contains("MSIE") || agent.contains("Trident")){
                downloadName = java.net.URLEncoder.encode(downloadName,"UTF-8");
            }else{
                // 非ie浏览器的处理
                downloadName = new String(downloadName.getBytes("UTF-8"),"ISO-8859-1");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // 设置响应的文件的名字和类型
        response.setHeader("Content-Disposition","attachment;filename=" + downloadName);

        // 设置压缩流：边压缩边下载
        ZipOutputStream zipos = null;
        try {
            zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            zipos.setMethod(ZipOutputStream.DEFLATED);
        }catch (Exception e){
            e.printStackTrace();
        }

        // 将文件写入压缩流
        DataOutputStream dos = null;

        for (DownloadFile downloadFile : downloadFiles) {

            Downloader downloader = fileOperatorFactory.getDownloader(downloadFile.getStorageType().getCode());
            InputStream stream = downloader.InputStream(downloadFile);
            try {
                zipos.putNextEntry(new ZipEntry(downloadFile.getFilename()));
                dos = new DataOutputStream(zipos);
                byte[] buffer = new byte[1024*1024];
                int readNum = 0;
                while ((readNum = stream.read(buffer)) != -1){
                    dos.write(buffer,0,readNum);
                }
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (stream != null){
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (dos != null){
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (zipos != null){
            try {
                zipos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @ApiOperation("文件移动")
    @PostMapping("/moveFile/{sourceId}/{targetId}/{userId}")
    /**
     *  sourceId：需要移动的文件的id
     *  targetId：目标目录的id
     */
    public CommonResult moveFile(@PathVariable("sourceId") Long sourceId,
                                 @PathVariable("targetId") Long targetId,
                                 @PathVariable("userId") Long userId){

        fileinfoService.moveFile(sourceId, targetId,userId);
        return CommonResult.ok();
    }


    @ApiOperation("文件批量移动")
    @PostMapping("/moveFilesBatch/{targetId}/{userId}")
    /**
     *  ids：批量移动的文件id
     *  targetId：目标目录的id
     */
    public CommonResult moveFilesBatch(@RequestBody List<Long> ids,
                                       @PathVariable("targetId") Long targetId,
                                       @PathVariable("userId") Long userId){

        for (Long id : ids) {
            fileinfoService.moveFile(id, targetId, userId);
        }

        return CommonResult.ok();
    }


    @ApiOperation("目录展示")
    @GetMapping("/showDir/{parentId}")
    public CommonResult showDir(@PathVariable("parentId") Long parentId){

        QueryWrapper<Fileinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentId);
        queryWrapper.select("id","name","parent_id");
        queryWrapper.eq("is_file","1");
        List<Fileinfo> list = fileinfoService.list(queryWrapper);
        List<DirVo> dirs = new ArrayList<>();
        for (Fileinfo fileinfo : list) {
            DirVo dirVo = new DirVo();
            BeanUtils.copyProperties(fileinfo,dirVo);
            dirs.add(dirVo);
        }

        return CommonResult.ok().data("dirs",dirs);
    }


}

