package com.github.jfcloud.jos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jfcloud.jos.entity.FileShare;
import com.github.jfcloud.jos.vo.ShareFileVo;
import com.github.jfcloud.jos.vo.ShowShareFileVo;

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

    Map<String,Object> shareFile(ShareFileVo shareFileVo);

    ShowShareFileVo showShareFile(String linkAddress, String extractCode);

    void saveShareFile(Long parentId, Long metadataId, String filename,Long userId);

    void updateView(Long shareId);

    void updateSave(Long shareId);

    void updateDownload(Long shareId);
}
