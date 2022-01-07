package com.github.jfcloud.jos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.jfcloud.jos.entity.Fileinfo;
import com.github.jfcloud.jos.entity.RecoveryFile;
import com.github.jfcloud.jos.service.FileinfoService;
import com.github.jfcloud.jos.service.RecoveryFileService;
import com.github.jfcloud.jos.util.CommonResult;
import com.github.jfcloud.jos.util.DownloadConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    /*@Autowired
    private FileinfoMapper fileinfoMapper;*/

    // test ok
    @ApiOperation("上传文件：断点上传")
    @PostMapping("/addFile/{parentId}")
    public CommonResult addFile(MultipartFile file,
                                @PathVariable("parentId") Long parentId,
                                HttpServletRequest request,HttpServletResponse response){

        boolean flag = fileinfoService.addFile(file, parentId,request,response);

        return flag ? CommonResult.ok() : CommonResult.error();
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
        String filePath = fileinfoService.downloadFile(id);
        if (filePath == null) return;

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

    /*@ApiOperation("判断当前目录下是否已经存在同名的文件")
    @GetMapping("/existsFile/{parentId}/{fileName}")
    public void existsFile(@PathVariable("parentId") Long parentId, @PathVariable("fileName") String fileName){
        boolean b = fileinfoService.existsFile(parentId, fileName);
        System.out.println(b);
    }*/

    /*@ApiOperation("递归查询")
    @GetMapping("/dfsFileList/{id}")
    public void dfsFileList(@PathVariable("id") Long id){
        List<Fileinfo> childList = fileinfoMapper.getChildList(id);
        for (Fileinfo fileinfo : childList) {
            System.out.println(fileinfo);
        }
    }*/

    /*@ApiOperation("上传文件")
    @PostMapping("/addfile")
    public void addfile(MultipartFile file){
        String md5 = FileSafeCode.getMD5(file);
        System.out.println(md5);
    }*/

    /*@ApiOperation("查看缓存数据")
    @GetMapping("/cache/{key}")
    public void cache(String key){
        Map<Object, Object> cacheMap = InitialCache.getCacheMap();
        for (Map.Entry<Object, Object> entry : cacheMap.entrySet()) {
            System.out.println("key:" + entry.getKey() + "---value:" + entry.getValue());
        }
    }*/

    /*@ApiOperation("获取重复文件")
    @GetMapping("/repeatFile/{parentId}/{filename}")
    public String repeatFile(@PathVariable("parentId") Long parentId,@PathVariable("filename") String filename){
        return fileinfoService.getRepeatFileName(parentId,filename);
    }*/

}

