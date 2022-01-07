package com.github.jfcloud.jos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jfcloud.jos.entity.Storage;
import com.github.jfcloud.jos.mapper.StorageMapper;
import com.github.jfcloud.jos.service.StorageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储方式表 服务实现类
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService {

}
