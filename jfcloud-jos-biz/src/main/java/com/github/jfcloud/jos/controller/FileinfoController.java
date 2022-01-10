package com.github.jfcloud.jos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.jfcloud.jos.entity.Fileinfo;
import com.github.jfcloud.jos.entity.Metadata;
import com.github.jfcloud.jos.entity.MultipartFileParam;
import com.github.jfcloud.jos.entity.RecoveryFile;
import com.github.jfcloud.jos.exception.BizException;
import com.github.jfcloud.jos.service.FileinfoService;
import com.github.jfcloud.jos.service.MetadataService;
import com.github.jfcloud.jos.service.RecoveryFileService;
import com.github.jfcloud.jos.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

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


    // test ok
    /*@ApiOperation("上传文件：断点上传")
    @PostMapping("/addFile/{parentId}")
    public CommonResult addFile(MultipartFile file,
                                @PathVariable("parentId") Long parentId,
                                HttpServletRequest request,HttpServletResponse response){

        boolean flag = fileinfoService.addFile(file, parentId,request,response);

        return flag ? CommonResult.ok() : CommonResult.error();
    }*/

    @ApiOperation("上传文件：分片上传")
    @PostMapping("/uploadFile/{parentId}")
    public CommonResult uploadFile(MultipartFileParam fileParam,
                                   @PathVariable("parentId") Long parentId){

        if (fileParam == null){
            return CommonResult.error().message("文件错误，请重新上传");
        }

        // 去元数据表中查询整个文件的md5码，如果存在，秒传
        String wholeIdentifier = fileParam.getWholeIdentifier();
        Metadata wholeMetadata = metadataService.findMetadataByMd5(wholeIdentifier);
        if (wholeMetadata != null){
            return CommonResult.ok().message("文件已经存在");
        }

        // 去后缀名
        String prename = fileParam.getFileName().substring(0,fileParam.getFileName().indexOf("."));

        // 文件不存在，去Redis缓存中获取
        // key：整个文件名+md5    value：当前切片+md5
        String key = prename + "-" + fileParam.getWholeIdentifier();
        String value = fileParam.getChunkNumber()+ "-" +fileParam.getIdentifier();
        if (redisUtil.hasKey(key)){
            List<Object> list = redisUtil.getList(key);
            for (Object o : list) {
                if (o.equals(value)){
                    CommonResult commonResult = CommonResult.ok().message("上传成功").data("下一块分片", fileParam.getChunkNumber() + 1);
                    if (fileParam.getChunkNumber() >= fileParam.getChunkSize()){
                        commonResult.data("是否合并切片","yes");
                    }else{
                        commonResult.data("是否合并切片","no");
                    }
                    return commonResult;
                }
            }
        }

        // 当前切片没有上传，那么将当前切片上传
        try {
            boolean upload = UploadUtil.uploadFile(fileParam.getFile(), key, value);
            if (!upload) return CommonResult.error().message("上传失败");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error().message("上传失败");
        }

        // 然后将当前切片的唯一标识放入到Redis中
        if (redisUtil.setList(key,value,60*60*24)){
            CommonResult commonResult = CommonResult.ok().message("当前分片上传成功").data("下一块分片", fileParam.getChunkNumber() + 1);
            if (fileParam.getChunkNumber() >= fileParam.getChunkSize()){
                commonResult.data("是否合并分片","yes");
            }else{
                commonResult.data("是否合并分片","no");
            }
            return commonResult;
        }else{
            redisUtil.removeValue(key,value);
            return CommonResult.error().message("文件错误，请重新上传");
        }

    }

    @ApiOperation("上传文件：合并切片")
    @PostMapping("/mergeFile/{filename}/{metadata}/{parentId}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult mergeFile(@PathVariable("filename")String filename,
                                  @PathVariable("metadata")String metadata,
                                  @PathVariable("parentId")Long parentId){

        // 将切片文件合并成一个文件
        if (filename == null) return CommonResult.error();
        File file = UploadUtil.mergeFile(filename, metadata);
        if (file == null) return CommonResult.error();

        // 删除临时文件
        UploadUtil.deleteTempFile(filename,metadata);

        // fileinfo表中添加记录
        Fileinfo fileinfo = new Fileinfo();
        Fileinfo parentFile = fileinfoService.getById(parentId);
        if (parentFile == null) throw new BizException("上传文件失败");
        if (parentFile.getId() != 1) fileinfo.setPath(parentFile.getPath() + "/" + parentFile.getName());
        fileinfo.setParentId(parentId);
        fileinfo.setName(fileinfoService.getRepeatFileName(parentId,filename));
        fileinfo.setIsFile("0");
        fileinfo.setFileSize((double) file.length());

        // 元数据表中添加记录
        Metadata metadata1 = new Metadata();
        metadata1.setMd5(metadata);
        metadata1.setSha1(FileSafeCode.getSha1(file));
        metadata1.setCrc32(FileSafeCode.getCRC32(file));
        metadata1.setLocalCtime(new Date());
        metadata1.setPath(fileinfo.getPath());
        metadata1.setFileSize(fileinfo.getFileSize());
        metadata1.setStatus("1");
        metadata1.setMimeType(UploadUtil.getMine(filename));
        metadata1.setMimeName(UploadUtil.getPro(filename));
        metadata1.setFileStoreKey(file.getAbsolutePath());

        metadataService.save(metadata1);
        fileinfo.setJosMetadataId(metadata1.getId());
        fileinfoService.save(fileinfo);

        return CommonResult.ok();
    }


    // test ok
    @ApiOperation("根据id查询文件信息")
    @GetMapping("/findFileById/{id}")
    public CommonResult findFileById(@PathVariable("id") Long id){
        Fileinfo fileinfo = fileinfoService.getById(id);
        return CommonResult.ok().data("文件信息",fileinfo);
    }

    // test ok
    @ApiOperation("根据parentId查询当前目录下的文件,并排序")
    @GetMapping("/findFileByParentId/{parentId}/{orderBy}")
    public CommonResult findFileByParentId(@PathVariable("parentId") Long parentId,
                                           @PathVariable("orderBy") String orderBy){
        List<Fileinfo> fileinfos = fileinfoService.findFileByParentId(parentId,orderBy);
        return CommonResult.ok().data("子文件",fileinfos);
    }


    // test ok
    @ApiOperation("新建一个空文件")
    @PostMapping("/createContext/{parentId}")
    public CommonResult createContext(@PathVariable("parentId") Long parentId,
                                  @RequestBody(required = false) Fileinfo fileinfo){
        boolean save = fileinfoService.createContext(parentId, fileinfo);
        return save ? CommonResult.ok().message("新建成功") : CommonResult.error().message("新建失败");
    }

    // test ok
    @ApiOperation("新建一个目录")
    @PostMapping("/createDir/{parentId}")
    public CommonResult createDir(@PathVariable("parentId") Long parentId,
                                  @RequestBody(required = false) Fileinfo fileinfo){
        boolean save = fileinfoService.createDir(parentId, fileinfo);
        return save ? CommonResult.ok().message("新建成功") : CommonResult.error().message("新建失败");
    }

    // test ok
    @ApiOperation("根据文件名查询文件")
    @GetMapping("/findFileByName/{name}")
    public CommonResult findFileByName(@PathVariable("name") String name){

        QueryWrapper<Fileinfo> qw = new QueryWrapper<>();
        qw.eq("name",name);
        List<Fileinfo> fileinfoList = fileinfoService.list(qw);
        
        return CommonResult.ok().data("文件信息",fileinfoList);
    }

    // test ok
    @ApiOperation("根据id删除文件")
    @DeleteMapping("/deleteFile/{id}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult deleteFile(@PathVariable("id") Long id){
        Fileinfo fileinfo = fileinfoService.getById(id);
        if (null == fileinfo){
            return CommonResult.error().message("文件不存在");
        }else{
            fileinfoService.removeFile(fileinfo);
            // 在回收站表中添加一条记录
            RecoveryFile recoveryFile = new RecoveryFile();
            recoveryFile.setFileinfoId(fileinfo.getId());
            recoveryFile.setDeletedBy(19980218L);
            recoveryFile.setDeletedDate(new Date());
            recoveryFileService.save(recoveryFile);
        }

        return CommonResult.ok();
    }

    // test ok
    @ApiOperation("根据id修改文件名")
    @PostMapping("/updateFile/{id}/{name}")
    public CommonResult updateFile(@PathVariable("id") Long id,
                                   @PathVariable("name") String name){

        boolean update = fileinfoService.updateFile(id, name);

        return update ? CommonResult.ok() : CommonResult.error();
    }


    // test ok
    @ApiOperation("根据id下载文件")
    @GetMapping("/downloadFile/{id}")
    public void downloadFile(@PathVariable("id") Long id,
                             HttpServletResponse response){

        // 获取文件的下载路径名
        Metadata metadata = fileinfoService.downloadFile(id);
        if (metadata == null) return;

        // 下载文件
        OutputStream os = null;
        FileInputStream input = null;
        try {
            File f = new File(filePath);
            input = new FileInputStream(f);
            byte[] buffer  = new byte[(int)f.length()];
            int offset = 0;
            int numRead = 0;
            while (offset<buffer.length&&(numRead-input.read(buffer,offset,buffer.length-offset))>=0) {
                offset+=numRead;
            }
            os = response.getOutputStream();
            response.setContentType(DownloadConstant.CONTENTTYPE);
            response.setHeader(DownloadConstant.HEADNAME, DownloadConstant.HEADVALUE+ URLEncoder.encode(f.getName(), DownloadConstant.HEADENCODE));
            os.write(buffer);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*@ApiOperation("测试分片上传")
    @PostMapping("/testSlice")
    public void testSlice(MultipartFile file){
        File file1 = new File("C:\\Users\\73561\\Desktop\\bioradar\\real\\" + "d41d8cd98f00b204e9800998ecf8427e");
        System.out.println(FileSafeCode.getMD5(file1)); // d41d8cd98f00b204e9800998ecf8427e
        System.out.println(FileSafeCode.getSha1(file1)); // da39a3ee5e6b4b0d3255bfef95601890afd80709
        System.out.println(FileSafeCode.getCRC32(file1)); // 0
    }*/

}

