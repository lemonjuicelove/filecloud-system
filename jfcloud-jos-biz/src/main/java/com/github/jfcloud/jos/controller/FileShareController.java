package com.github.jfcloud.jos.controller;


import com.github.jfcloud.jos.core.common.StorageTypeEnum;
import com.github.jfcloud.jos.core.factory.FileOperatorFactory;
import com.github.jfcloud.jos.core.operation.download.Downloader;
import com.github.jfcloud.jos.core.operation.download.entity.DownloadFile;
import com.github.jfcloud.jos.entity.Metadata;
import com.github.jfcloud.jos.exception.BizException;
import com.github.jfcloud.jos.service.FileShareService;
import com.github.jfcloud.jos.service.MetadataService;
import com.github.jfcloud.jos.util.CommonResult;
import com.github.jfcloud.jos.vo.SaveShareFileVo;
import com.github.jfcloud.jos.vo.ShareFileVo;
import com.github.jfcloud.jos.vo.ShowShareFileVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private FileOperatorFactory fileOperatorFactory;


    @ApiOperation("文件分享")
    @PostMapping("/shareFile")
    /*
        fileId：文件id
        time：有效天数
     */
    public CommonResult shareFile(@RequestBody ShareFileVo shareFileVo){

        Map<String, Object> shareInfo = fileShareService.shareFile(shareFileVo);

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
        ShowShareFileVo showShareFileVo = fileShareService.showShareFile(linkAddress, extractCode);

        // share文件中的view+1
        fileShareService.updateView(showShareFileVo.getShareId());

        return CommonResult.ok().data("分享文件信息",showShareFileVo);
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
    @PostMapping("/downloadShareFile")
    /*
        parentId：保存的位置
        metadataId：元数据id
     */
    public void downloadShareFile(@RequestBody SaveShareFileVo saveShareFileVo,
                                  HttpServletRequest request,
                                  HttpServletResponse response){

        if (saveShareFileVo == null) throw new BizException("下载分享文件失败");
        Metadata metadata = metadataService.getById(saveShareFileVo.getMetadataId());
        if (metadata == null) throw new BizException("下载分享文件失败");

        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFilename(saveShareFileVo.getFilename());
        downloadFile.setMetadata(String.valueOf(metadata.getId()));

        // 下载文件
        Downloader downloader = fileOperatorFactory.getDownloader(metadata.getJosStorageId());
        downloader.download(downloadFile,request,response);

        // 下载成功，share表中的save+1
        fileShareService.updateDownload(saveShareFileVo.getShareFileId());
    }


}

