package com.github.jfcloud.jos.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jfcloud.jos.entity.FileShare;
import com.github.jfcloud.jos.entity.FileShareLink;
import com.github.jfcloud.jos.entity.Fileinfo;
import com.github.jfcloud.jos.entity.Metadata;
import com.github.jfcloud.jos.exception.BizException;
import com.github.jfcloud.jos.mapper.FileShareMapper;
import com.github.jfcloud.jos.service.FileShareLinkService;
import com.github.jfcloud.jos.service.FileShareService;
import com.github.jfcloud.jos.service.FileinfoService;
import com.github.jfcloud.jos.service.MetadataService;
import com.github.jfcloud.jos.util.DateUtil;
import com.github.jfcloud.jos.vo.ShareFileVo;
import com.github.jfcloud.jos.vo.ShowShareFileVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    private FileinfoService fileinfoService;

    @Autowired
    private FileShareLinkService fileShareLinkService;

    @Autowired
    private MetadataService metadataService;

    // 分享文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> shareFile(ShareFileVo shareFileVo) {

        if (shareFileVo == null) throw new BizException("分享文件错误");

        Fileinfo fileinfo = fileinfoService.getById(shareFileVo.getId());
        if (fileinfo == null || "1".equals(fileinfo.getIsFile())) throw new BizException("分享文件错误");

        // share表添加记录
        FileShare fileShare = new FileShare();
        fileShare.setName(fileinfo.getName());
        fileShare.setEffectiveDate(DateUtil.addDays(new Date(),shareFileVo.getTime()));
        // 生成提取码和链接
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extractCode = RandomUtil.randomNumbers(6);
        fileShare.setLinkAddress(uuid);
        fileShare.setExtractCode(extractCode);
        fileShare.setSharer(shareFileVo.getName());
        fileShare.setSharerId(shareFileVo.getUserId());
        fileShare.setShareTime(new Date());
        fileShare.setIsEffective("1");
        fileShare.setMaxCount(shareFileVo.getMaxCount());

        boolean save = this.save(fileShare);

        // share_link表添加记录
        FileShareLink fileShareLink = new FileShareLink();
        fileShareLink.setShareId(fileShare.getId());
        fileShareLink.setFileId(fileinfo.getId());
        fileShareLink.setCreatedBy(shareFileVo.getUserId());
        boolean save1 = fileShareLinkService.save(fileShareLink);

        if (!save || !save1){
            throw new BizException("分享文件错误");
        }

        Map<String,Object> res = new HashMap<>();
        res.put("链接",uuid);
        res.put("提取码",extractCode);

        return res;
    }

    // 查看分享文件的信息
    @Override
    public ShowShareFileVo showShareFile(String linkAddress, String extractCode) {

        // 根据链接和提取码去share表和share_link表中查数据，并封装成vo
        QueryWrapper<FileShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("link_address",linkAddress);
        queryWrapper.eq("extract_code",extractCode);
        queryWrapper.eq("is_effective","1");
        FileShare fileShare = this.getOne(queryWrapper);
        if (fileShare == null) throw new BizException("文件不存在");

        // 验证有效期是否过期
        // 验证访问人数是否超过限制
        if (fileShare.getEffectiveDate().before(new Date()) || fileShare.getViewCount() > fileShare.getMaxCount()){
            // 失效
            fileShare.setIsEffective("0");
            this.updateById(fileShare);
            throw new BizException("文件失效");
        }

        // 去share_link表中查数据
        QueryWrapper<FileShareLink> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("share_id",fileShare.getId());
        FileShareLink fileShareLink = fileShareLinkService.getOne(queryWrapper1);
        if (fileShareLink == null) throw new BizException("文件不存在");

        // 去fileinfo表中查数据
        Fileinfo fileinfo = fileinfoService.getById(fileShareLink.getFileId());
        if (fileinfo == null) throw new BizException("文件不存在");

        ShowShareFileVo showShareFileVo = new ShowShareFileVo();
        showShareFileVo.setName(fileinfo.getName());
        showShareFileVo.setShareId(fileShare.getId());
        showShareFileVo.setMetadataId(fileinfo.getJosMetadataId());
        showShareFileVo.setFileSize(fileinfo.getFileSize());
        showShareFileVo.setShareTime(fileShare.getShareTime());
        showShareFileVo.setEndTime(fileShare.getEffectiveDate());
        showShareFileVo.setUserName(fileShare.getSharer());

        return showShareFileVo;
    }

    // 保存分享的文件
    // 存在一些问题：如果是通用的系统，那么就不需要做文件分享了，因为目录都是相同的
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveShareFile(Long parentId, Long metadataId, String filename,Long userId) {

        if (filename == null) throw new BizException("保存文件失败");
        Metadata sourceMetadata = metadataService.getById(metadataId);
        if (sourceMetadata == null) throw new BizException("保存文件失败");
        Fileinfo parentFile = fileinfoService.getById(parentId);
        if (parentFile == null) throw new BizException("保存文件失败");

        // fileinfo表中创建记录，关联元数据表中新创建的记录
        Fileinfo fileinfo = new Fileinfo();
        fileinfo.setName(fileinfoService.getRepeatFileName(parentId,filename));
        fileinfo.setParentId(parentId);
        fileinfo.setIsFile("0");
        fileinfo.setFileSize(sourceMetadata.getFileSize());
        fileinfo.setFileAuther(userId);
        fileinfo.setPath(parentFile.getPath() + "/" + parentFile.getName());

        // 元数据表中创建记录，id不同，其他的应该都相同
        Metadata metadata = new Metadata();
        BeanUtils.copyProperties(sourceMetadata,metadata);
        metadata.setId(null);
        metadata.setPath(fileinfo.getPath());
        metadata.setLocalCtime(new Date());

        boolean save = metadataService.save(metadata);

        fileinfo.setJosMetadataId(metadata.getId());
        boolean save1 = fileinfoService.save(fileinfo);

        if (!save || !save1) throw new BizException("保存文件失败");

    }

    // 更改share表中的viewCount
    @Override
    public void updateView(Long shareId) {
        FileShare fileShare = this.getById(shareId);
        if (fileShare == null) return;
        fileShare.setViewCount(fileShare.getViewCount()+1);
         this.updateById(fileShare);
    }

    // 更改share表中的saveCount
    @Override
    public void updateSave(Long shareId) {
        FileShare fileShare = this.getById(shareId);
        if (fileShare == null) return;
        fileShare.setSaveCount(fileShare.getSaveCount()+1);
        this.updateById(fileShare);
    }

    // 更改share表中的downloadCount
    @Override
    public void updateDownload(Long shareId) {
        FileShare fileShare = this.getById(shareId);
        if (fileShare == null) return;
        fileShare.setDownloadCount(fileShare.getDownloadCount()+1);
        this.updateById(fileShare);
    }

}
