package com.github.jfcloud.jos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jfcloud.jos.entity.Fileinfo;
import com.github.jfcloud.jos.entity.Metadata;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

    boolean createDir(Long parentId, Fileinfo fileinfo);

    boolean removeFile(Fileinfo fileinfo);

    // boolean addFile(MultipartFile file, Long parentId, HttpServletRequest request, HttpServletResponse response);

    boolean createContext(Long parentId, Fileinfo fileinfo);

    boolean updateFile(Long id, String name);

    Metadata downloadFile(Long id);

    // boolean existsFile(Long parentId, String fileName);

    List<Fileinfo> listDeletedByIds(List<Long> ids);

    List<Fileinfo> getDeletedChildList(Long id);

    void recoveryFile(List<Long> ids);

    String getRepeatFileName(Long parentId, String fileName);
}
