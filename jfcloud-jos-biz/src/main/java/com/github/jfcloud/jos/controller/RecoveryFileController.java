package com.github.jfcloud.jos.controller;


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

        return CommonResult.ok().data("recoveryFileVos",recoveryFileVos);
    }

    /*@ApiOperation("添加删除文件记录")
    @PostMapping("/addRecoveryFile")
    public CommonResult addRecoveryFile(@RequestBody RecoveryFile recoveryFile){

        boolean save = recoveryFileService.save(recoveryFile);

        return save ? CommonResult.ok() : CommonResult.error();
    }*/

    // fileinfo表中的记录和元数据表中的记录是否需要删除？
    @ApiOperation("彻底删除文件记录")
    @PostMapping("/deletedFile/{recoveryFileId}")
    public CommonResult deletedFile(@PathVariable("recoveryFileId") Long id){
        boolean remove = recoveryFileService.removeById(id);
        return remove ? CommonResult.ok() : CommonResult.error();
    }

    @ApiOperation("恢复文件记录")
    @PostMapping("/recoveryFile/{recoveryFileId}")
    public CommonResult recoveryFile(@PathVariable("recoveryFileId") Long id){
        boolean recovery = recoveryFileService.recoveryFile(id);
        return recovery ? CommonResult.ok() : CommonResult.error();
    }


}

