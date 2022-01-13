package com.github.jfcloud.jos.controller;


import com.github.jfcloud.jos.entity.Metadata;
import com.github.jfcloud.jos.service.FileShareService;
import com.github.jfcloud.jos.service.MetadataService;
import com.github.jfcloud.jos.util.CommonResult;
import com.github.jfcloud.jos.util.DownloadConstant;
import com.github.jfcloud.jos.util.UploadUtil;
import com.github.jfcloud.jos.vo.SaveShareFileVo;
import com.github.jfcloud.jos.vo.ShareFileVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

/**
 * <p>
 * 文件分享表 前端控制器
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Api("文件分享")
@RestController
@RequestMapping("/jos/fileShare")
public class FileShareController {

    @Autowired
    private FileShareService fileShareService;

    @Autowired
    private MetadataService metadataService;


    @ApiOperation("文件分享")
    @GetMapping("/shareFile/{fileId}/{time}")
    /*
        fileId：文件id
        time：有效天数
     */
    public CommonResult shareFile(@PathVariable("fileId") Long id,
                                  @PathVariable("time") Integer time){


        Map<String, Object> shareInfo = fileShareService.shareFile(id, time);

        return CommonResult.ok().data(shareInfo);
    }


    @ApiOperation("通过链接和提取码获取分享文件")
    @GetMapping("/showShareFile/{linkAddress}/{extractCode}")
    /*
        linkAddress：链接
        extractCode：提取码
     */
    public CommonResult showShareFile(@PathVariable("linkAddress") String linkAddress,
                                      @PathVariable("extractCode") String extractCode){

        // 根据链接和提取码去share表和share_link表中查数据，并封装成vo
        ShareFileVo shareFileVo = fileShareService.showShareFile(linkAddress, extractCode);

        // share文件中的view+1
        fileShareService.updateView(shareFileVo.getShareId());

        return CommonResult.ok().data("分享文件",shareFileVo);
    }

    @ApiOperation("保存分享文件")
    @PostMapping("/saveShareFile")
    /*
        parentId：保存的位置
        metadataId：元数据id
     */
    public CommonResult saveShareFile(@RequestBody SaveShareFileVo saveShareFileVo){

        if (saveShareFileVo == null) return CommonResult.error();

        fileShareService.saveShareFile(saveShareFileVo.getParentId(),saveShareFileVo.getMetadataId(),saveShareFileVo.getFilename());

        // 保存成功，share表中的save+1
        fileShareService.updateSave(saveShareFileVo.getShareFileId());

        return CommonResult.ok();
    }

    @ApiOperation("下载分享文件")
    @GetMapping("/downloadShareFile/{metadataId}/{filename}/{shareFileId}")
    /*
        parentId：保存的位置
        metadataId：元数据id
     */
    public void downloadShareFile(@PathVariable("metadataId") Long metadataId,
                                          @PathVariable("filename") String filename,
                                          @PathVariable("shareFileId") Long shareFileId,
                                          HttpServletResponse response){

        Metadata metadata = metadataService.getById(metadataId);
        if (metadata == null) return;

        // 下载文件
        UploadUtil.downloadFile(filename,metadata.getFileStoreKey(),response);

        // 下载成功，share表中的save+1
        fileShareService.updateDownload(shareFileId);
    }

}

