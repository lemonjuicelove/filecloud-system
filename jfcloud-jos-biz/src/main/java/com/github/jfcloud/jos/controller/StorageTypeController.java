package com.github.jfcloud.jos.controller;


import com.github.jfcloud.jos.service.StorageTypeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 存储方式类型表 前端控制器
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Api("存储方式类型管理")
@RestController
@RequestMapping("/jos/storage-type")
public class StorageTypeController {

    @Autowired
    private StorageTypeService storageTypeService;
}

