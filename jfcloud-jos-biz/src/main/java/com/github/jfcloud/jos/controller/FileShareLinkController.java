package com.github.jfcloud.jos.controller;


import com.github.jfcloud.jos.service.FileShareLinkService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 文件分享关联表 前端控制器
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Api("文件分享关联")
@RestController
@RequestMapping("/jos/file-share-link")
public class FileShareLinkController {

    @Autowired
    private FileShareLinkService fileShareLinkService;

}

