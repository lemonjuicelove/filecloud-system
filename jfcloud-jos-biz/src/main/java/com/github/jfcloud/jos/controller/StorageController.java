package com.github.jfcloud.jos.controller;


import com.github.jfcloud.jos.service.StorageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 存储方式表 前端控制器
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Api("存储方式管理")
@RestController
@RequestMapping("/jos/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;
}

