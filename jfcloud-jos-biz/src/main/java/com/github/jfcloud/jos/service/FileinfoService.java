package com.github.jfcloud.jos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jfcloud.jos.core.operation.download.entity.DownloadFile;
import com.github.jfcloud.jos.entity.Fileinfo;
import com.github.jfcloud.jos.entity.Metadata;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lemon
 * @since 2021-12-28
 */
public interface FileinfoService extends IService<Fileinfo> {

    List<Fileinfo> findFileByParentId(Long parentId, String orderBy);

    boolean createDir(Long parentId, Long userId, String filename);

    boolean removeFile(Fileinfo fileinfo,Long userId);

    boolean createContext(Long parentId,Long userId, String filename);

    boolean updateFile(Long id, String name,Long userId);

    DownloadFile downloadFile(Long id);

    List<Fileinfo> listDeletedByIds(List<Long> ids);

    List<Fileinfo> getDeletedChildList(Long id);

    void recoveryFile(List<Long> ids);

    String getRepeatFileName(Long parentId, String fileName);

    void deleteFiles(List<Long> ids);

    void moveFile(Long sourceId, Long targetId,Long userId);

    List<DownloadFile> downloadFilesBatch(List<Long> ids);
}
