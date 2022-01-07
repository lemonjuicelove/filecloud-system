package com.github.jfcloud.jos.controller;


import com.github.jfcloud.jos.service.MetadataService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 元数据表 前端控制器
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Api("元数据管理")
@RestController
@RequestMapping("/jos/metadata")
public class MetadataController {

    @Autowired
    private MetadataService metadataService;

}

