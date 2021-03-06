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
 *  ???????????????
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Api("????????????")
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

    @ApiOperation("????????????")
    @PostMapping("/uploadFile/{parentId}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult uploadFile(UploadFile uploadFile,@PathVariable("parentId") Long parentId){

        if (uploadFile == null) return CommonResult.error().message("??????????????????");

        Fileinfo parentFileinfo = fileinfoService.getById(parentId);
        if (parentFileinfo == null) return CommonResult.error().message("??????????????????");

        // ???????????????????????????????????????md???????????????????????????
        String wholeIdentifier = uploadFile.getWholeIdentifier();
        Metadata metadata = metadataService.findMetadataByMd5(wholeIdentifier);

        if (metadata != null) { // ???????????????

            // fileinfo??????????????????
            Fileinfo fileinfo = new Fileinfo();
            fileinfo.setPath(parentFileinfo.getPath() + "/" + parentFileinfo.getName());
            fileinfo.setParentId(parentId);
            fileinfo.setName(fileinfoService.getRepeatFileName(parentId,uploadFile.getFileName()));
            fileinfo.setIsFile("0");
            fileinfo.setFileSize(metadata.getFileSize());

            // ???????????????????????????
            Metadata newMetadata = new Metadata();
            newMetadata.setMd5(wholeIdentifier);
            newMetadata.setPath(fileinfo.getPath());
            newMetadata.setFileSize(metadata.getFileSize());
            newMetadata.setMimeType(FileUtil.getMine(uploadFile.getFileName()));
            newMetadata.setMimeName(FileUtil.getPro(uploadFile.getFileName()));
            newMetadata.setLocalCtime(new Date());
            newMetadata.setJosStorageId(metadata.getJosStorageId());
            newMetadata.setFileStoreKey(metadata.getFileStoreKey());

            metadataService.save(newMetadata);
            fileinfo.setJosMetadataId(newMetadata.getId());
            fileinfoService.save(fileinfo);

            UploadFileResults result = new UploadFileResults();
            BeanUtils.copyProperties(uploadFile,result);
            result.setStatus(UploadFileStatusEnum.SUCCESS);
            return CommonResult.ok().data("result",result);
        }


        // ???????????????????????????????????????
        String pre = FileUtil.getPre(uploadFile.getFileName());
        String key = pre + "-" + wholeIdentifier;
        String value = uploadFile.getChunkNumber() + "-" + uploadFile.getIdentifier();
        if (redisUtil.hasKey(key)){
            List<Object> values = redisUtil.getList(key);
            for (Object o : values) {
                if (o.equals(value)){
                    UploadFileResults result = new UploadFileResults();
                    BeanUtils.copyProperties(uploadFile,result);
                    result.setStatus(UploadFileStatusEnum.UNCOMPLATE);
                    return CommonResult.ok().data("result",result);
                }
            }
        }

        Uploader uploader = fileOperatorFactory.getUploader();
        UploadFileResults uploadResults = uploader.upload(uploadFile);

        if (uploadResults.getStatus() == UploadFileStatusEnum.UNCOMPLATE){ // ????????????????????????
            redisUtil.setList(key,value,60*60*24);
            return CommonResult.ok().data("result",uploadResults);
        }else if (uploadResults.getStatus() == UploadFileStatusEnum.FAIL){ // ????????????????????????
            return CommonResult.error().data("result",uploadResults).message("??????????????????");
        }

        redisUtil.del(key);

        // fileinfo??????????????????
        Fileinfo fileinfo = new Fileinfo();
        fileinfo.setPath(parentFileinfo.getPath() + "/" + parentFileinfo.getName());
        fileinfo.setParentId(parentId);
        fileinfo.setName(fileinfoService.getRepeatFileName(parentId,uploadFile.getFileName()));
        fileinfo.setIsFile("0");
        fileinfo.setFileSize(uploadResults.getFileSize());

        // ???????????????????????????
        Metadata newMetadata = new Metadata();
        newMetadata.setMd5(wholeIdentifier);
        newMetadata.setPath(fileinfo.getPath());
        newMetadata.setFileSize(uploadResults.getFileSize());
        newMetadata.setMimeType(FileUtil.getMine(uploadFile.getFileName()));
        newMetadata.setMimeName(FileUtil.getPro(uploadFile.getFileName()));
        newMetadata.setLocalCtime(new Date());
        newMetadata.setJosStorageId(uploadResults.getStorageType().getCode());
        newMetadata.setFileStoreKey(uploadResults.getFileUrl());

        metadataService.save(newMetadata);
        fileinfo.setJosMetadataId(newMetadata.getId());
        fileinfoService.save(fileinfo);

        return CommonResult.ok().data("result",uploadResults);
    }


    @ApiOperation("??????id??????????????????")
    @GetMapping("/findFileById/{id}")
    public CommonResult findFileById(@PathVariable("id") Long id){
        Fileinfo fileinfo = fileinfoService.getById(id);
        return CommonResult.ok().data("????????????",fileinfo);
    }


    @ApiOperation("??????parentId??????????????????????????????,?????????")
    @GetMapping("/findFileByParentId/{parentId}/{orderBy}")
    public CommonResult findFileByParentId(@PathVariable("parentId") Long parentId,
                                           @PathVariable("orderBy") String orderBy){

        if (StringUtils.isEmpty(orderBy)){
            orderBy = "time";
        }else if (!"time".equals(orderBy) && !"size".equals(orderBy) && !"name".equals(orderBy)){
            throw new BizException("??????????????????");
        }

        List<Fileinfo> fileinfos = fileinfoService.findFileByParentId(parentId,orderBy);
        return CommonResult.ok().data("?????????",fileinfos);
    }


    @ApiOperation("?????????????????????")
    @PostMapping("/createContext/{parentId}")
    public CommonResult createContext(@PathVariable("parentId") Long parentId,
                                  @RequestBody Fileinfo fileinfo){
        boolean save = fileinfoService.createContext(parentId, fileinfo);
        return save ? CommonResult.ok() : CommonResult.error().message("????????????");
    }


    @ApiOperation("??????????????????")
    @PostMapping("/createDir/{parentId}")
    public CommonResult createDir(@PathVariable("parentId") Long parentId,
                                  @RequestBody Fileinfo fileinfo){
        boolean save = fileinfoService.createDir(parentId, fileinfo);
        return save ? CommonResult.ok() : CommonResult.error().message("????????????");
    }


    @ApiOperation("???????????????????????????")
    @GetMapping("/findFileByName/{name}")
    public CommonResult findFileByName(@PathVariable("name") String name){

        QueryWrapper<Fileinfo> qw = new QueryWrapper<>();
        qw.eq("name",name);
        List<Fileinfo> fileinfoList = fileinfoService.list(qw);
        
        return CommonResult.ok().data("????????????",fileinfoList);
    }


    @ApiOperation("??????id????????????")
    @DeleteMapping("/deleteFile/{id}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult deleteFile(@PathVariable("id") Long id){
        Fileinfo fileinfo = fileinfoService.getById(id);
        if (null == fileinfo){
            return CommonResult.error().message("???????????????");
        }else{

            boolean remove = fileinfoService.removeFile(fileinfo);
            // ????????????????????????????????????
            RecoveryFile recoveryFile = new RecoveryFile();
            recoveryFile.setFileinfoId(fileinfo.getId());
            recoveryFile.setDeletedBy(19980218L);
            recoveryFile.setDeletedDate(new Date());
            boolean save = recoveryFileService.save(recoveryFile);

            if (remove && save){
                return CommonResult.ok();
            }else{
                throw new BizException("??????????????????");
            }
        }

    }


    @ApiOperation("??????id??????????????????")
    @DeleteMapping("/deleteFilesBatch")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult deleteFilesBatch(@RequestBody List<Long> ids){

        for (Long id : ids) {
            Fileinfo fileinfo = fileinfoService.getById(id);
            if (fileinfo != null){
                boolean remove = fileinfoService.removeFile(fileinfo);
                // ????????????????????????????????????
                RecoveryFile recoveryFile = new RecoveryFile();
                recoveryFile.setFileinfoId(fileinfo.getId());
                recoveryFile.setDeletedBy(19980218L);
                recoveryFile.setDeletedDate(new Date());
                boolean save = recoveryFileService.save(recoveryFile);

                if (!remove || !save) throw new BizException("??????????????????");
            }
        }

        return CommonResult.ok();
    }


    @ApiOperation("??????id???????????????")
    @PostMapping("/updateFile/{id}/{name}")
    public CommonResult updateFile(@PathVariable("id") Long id,
                                   @PathVariable("name") String name){

        if (name == null) return CommonResult.error().message("?????????????????????");

        boolean update = fileinfoService.updateFile(id, name);

        return update ? CommonResult.ok() : CommonResult.error().message("????????????");
    }


    @ApiOperation("??????id????????????")
    @GetMapping("/downloadFile/{id}")
    public void downloadFile(@PathVariable("id") Long id,
                             HttpServletRequest request,
                             HttpServletResponse response){

        // ???????????????????????????????????????
        DownloadFile downloadFile = fileinfoService.downloadFile(id);
        if (downloadFile == null) throw new DownloadException("?????????????????????");

        // ????????????
        Downloader downloader = fileOperatorFactory.getDownloader(downloadFile.getStorageType().getCode());
        downloader.download(downloadFile,request,response);
    }


    @ApiOperation("??????????????????")
    @PostMapping("/downloadFilesBatch/{id}")
    public void downloadFilesBatch(@RequestBody List<Long> ids,
                             HttpServletRequest request,
                             HttpServletResponse response){

        List<DownloadFile> downloadFiles = fileinfoService.downloadFilesBatch(ids);
        if (downloadFiles.size() == 0) throw new DownloadException("?????????????????????");;

        response.reset();
        // ???????????????????????????
        response.setCharacterEncoding("utf-8");
        // ???????????????????????????
        response.setContentType("PPLICATION/OCTET-STREAM");

        // ????????????????????????
        String dates = DateUtil.formatDate(new Date());
        String billname = "?????????-" + dates;
        String downloadName = billname + ".zip";

        // ????????????????????????????????????????????????????????????
        String agent = request.getHeader("USER-AGENT");
        try{
            // ?????????ie?????????????????????
            if (agent.contains("MSIE") || agent.contains("Trident")){
                downloadName = java.net.URLEncoder.encode(downloadName,"UTF-8");
            }else{
                // ???ie??????????????????
                downloadName = new String(downloadName.getBytes("UTF-8"),"ISO-8859-1");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // ???????????????????????????????????????
        response.setHeader("Content-Disposition","attachment;filename=" + downloadName);

        // ????????????????????????????????????
        ZipOutputStream zipos = null;
        try {
            zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            zipos.setMethod(ZipOutputStream.DEFLATED);
        }catch (Exception e){
            e.printStackTrace();
        }

        // ????????????????????????
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


    @ApiOperation("????????????")
    @PostMapping("/moveFile/{sourceId}/{targetId}")
    /**
     *  sourceId???????????????????????????id
     *  targetId??????????????????id
     */
    public CommonResult moveFile(@PathVariable("sourceId") Long sourceId,
                                 @PathVariable("targetId") Long targetId){

        fileinfoService.moveFile(sourceId, targetId);
        return CommonResult.ok();
    }


    @ApiOperation("??????????????????")
    @PostMapping("/moveFilesBatch/{targetId}")
    /**
     *  ids????????????????????????id
     *  targetId??????????????????id
     */
    public CommonResult moveFilesBatch(@RequestBody List<Long> ids,
                                 @PathVariable("targetId") Long targetId){

        for (Long id : ids) {
            fileinfoService.moveFile(id, targetId);
        }

        return CommonResult.ok();
    }


    @ApiOperation("????????????")
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

