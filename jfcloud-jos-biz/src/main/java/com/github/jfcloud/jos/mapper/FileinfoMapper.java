package com.github.jfcloud.jos.mapper;

import com.github.jfcloud.jos.entity.Fileinfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
@Mapper
public interface FileinfoMapper extends BaseMapper<Fileinfo> {

    void removeFile(@Param("list") List<Long> ids, @Param("deletedBy") Long deletedBy, @Param("date") Date date);

    List<Fileinfo> getChildList(Long id);

    List<Fileinfo> getDeletedChildList(Long id);

    List<Fileinfo> listDeletedByIds(@Param("list") List<Long> ids);

    void recoveryFile(@Param("list") List<Long> ids);

    void deleteFiles(@Param("list")List<Long> ids);
}
