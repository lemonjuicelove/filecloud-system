package com.github.jfcloud.jos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jfcloud.jos.entity.Metadata;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 元数据表 服务类
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
public interface MetadataService extends IService<Metadata> {


    Metadata findMetadataByMd5(String md5);

    void updateStatues(List<Long> ids, Long modifiedBy, Date modifiedDate);

}
