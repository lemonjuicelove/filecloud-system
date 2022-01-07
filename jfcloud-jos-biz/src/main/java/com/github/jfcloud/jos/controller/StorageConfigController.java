package com.github.jfcloud.jos.controller;


import com.github.jfcloud.jos.service.StorageConfigService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 存储方式实例参数表 前端控制器
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Api("存储方式参数管理")
@RestController
@RequestMapping("/jos/storage-config")
public class StorageConfigController {

    @Autowired
    private StorageConfigService storageConfigService;

}

