package com.github.jfcloud.jos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jfcloud.jos.entity.StorageType;
import com.github.jfcloud.jos.mapper.StorageTypeMapper;
import com.github.jfcloud.jos.service.StorageTypeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储方式类型表 服务实现类
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Service
public class StorageTypeServiceImpl extends ServiceImpl<StorageTypeMapper, StorageType> implements StorageTypeService {

}
