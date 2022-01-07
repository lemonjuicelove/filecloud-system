package com.github.jfcloud.jos.controller;


import com.github.jfcloud.jos.service.FileShareService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

