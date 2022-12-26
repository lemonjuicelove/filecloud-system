package com.github.jfcloud.jos.mapper;

import com.github.jfcloud.jos.entity.Metadata;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 元数据表 Mapper 接口
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Mapper
public interface MetadataMapper extends BaseMapper<Metadata> {

    void removeMetadata(@Param("list") List<Long> ids, @Param("deletedBy") Long deletedBy, @Param("date") Date date);

    void recoveryMeta(@Param("list") List<Long> ids);

    void deleteMeta(@Param("list") List<Long> ids);
}
