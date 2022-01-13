package com.github.jfcloud.jos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jfcloud.jos.entity.FileShare;
import com.github.jfcloud.jos.vo.ShareFileVo;

import java.util.Map;

/**
 * <p>
 * 文件分享表 服务类
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
public interface FileShareService extends IService<FileShare> {

    Map<String,Object> shareFile(Long id, Integer time);

    ShareFileVo showShareFile(String linkAddress, String extractCode);

    boolean saveShareFile(Long parentId, Long metadataId, String filename);

    void updateView(Long shareId);

    void updateSave(Long shareId);

    void updateDownload(Long shareId);
}
