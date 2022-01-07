package com.github.jfcloud.jos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jfcloud.jos.entity.FileShare;
import com.github.jfcloud.jos.mapper.FileShareMapper;
import com.github.jfcloud.jos.service.FileShareService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文件分享表 服务实现类
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Service
public class FileShareServiceImpl extends ServiceImpl<FileShareMapper, FileShare> implements FileShareService {

}
