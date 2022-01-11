package com.github.jfcloud.jos.controller;


import cn.hutool.core.util.IdUtil;
import com.github.jfcloud.jos.service.RecoveryFileService;
import com.github.jfcloud.jos.util.CommonResult;
import com.github.jfcloud.jos.vo.RecoveryFileVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lemon
 * @since 2022-01-04
 */
@RestController
@RequestMapping("/jos/recovery-file")
public class RecoveryFileController {

    @Autowired
    private RecoveryFileService recoveryFileService;

    @ApiOperation("回收站文件列表")
    @GetMapping("/recoveryFileList")
    public CommonResult recoveryFileList(){

        List<RecoveryFileVo> recoveryFileVos = recoveryFileService.recoveryFileList();
        for (RecoveryFileVo vo : recoveryFileVos) {
            System.out.println(vo.getRecoveryFileId());
            System.out.println(vo.getFileinfoId());
        }

        return CommonResult.ok().data("recoveryFileVos",recoveryFileVos);
    }


    @ApiOperation("彻底删除文件记录")
    @PostMapping("/deleteFile/{recoveryFileId}")
    public CommonResult deleteFile(@PathVariable("recoveryFileId") Long id){
        boolean remove = recoveryFileService.deleteFile(id);
        return remove ? CommonResult.ok() : CommonResult.error();
    }

    @ApiOperation("恢复文件记录")
    @PostMapping("/recoveryFile/{recoveryFileId}")
    public CommonResult recoveryFile(@PathVariable("recoveryFileId") Long id){
        boolean recovery = recoveryFileService.recoveryFile(id);
        return recovery ? CommonResult.ok() : CommonResult.error();
    }

    @ApiOperation("批量删除")
    @PostMapping("/deleteFilesBatch")
    public CommonResult deleteFilesBatch(@RequestBody List<Long> ids){
        boolean recovery = recoveryFileService.deleteFilesBatch(ids);
        return recovery ? CommonResult.ok() : CommonResult.error();
    }

    @ApiOperation("批量恢复")
    @PostMapping("/recoveryFilesBatch")
    public CommonResult recoveryFilesBatch(@RequestBody List<Long> ids){
        boolean remove = recoveryFileService.recoveryFilesBatch(ids);
        return remove ? CommonResult.ok() : CommonResult.error();
    }


}

