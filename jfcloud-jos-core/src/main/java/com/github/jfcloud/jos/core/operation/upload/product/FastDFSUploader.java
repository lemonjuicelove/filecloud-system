package com.github.jfcloud.jos.core.operation.upload.product;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.jfcloud.jos.core.constant.StorageTypeEnum;
import com.github.jfcloud.jos.core.constant.UploadFileStatusEnum;
import com.github.jfcloud.jos.core.exception.operation.UploadException;
import com.github.jfcloud.jos.core.operation.upload.Uploader;
import com.github.jfcloud.jos.core.operation.upload.domain.UploadFile;
import com.github.jfcloud.jos.core.operation.upload.domain.UploadFileResult;
import com.github.jfcloud.jos.core.operation.upload.request.QiwenMultipartFile;
import com.github.jfcloud.jos.core.util.RedisUtils;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import com.github.tobato.fastdfs.service.DefaultAppendFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FastDFSUploader extends Uploader {

//    @Resource
    private AppendFileStorageClient defaultAppendFileStorageClient = new DefaultAppendFileStorageClient();

    @Resource
    RedisUtils redisUtils;

    @Override
    public void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) throws IOException {
        StorePath storePath = null;

        if (uploadFile.getChunkNumber() <= 1) {
            log.info("上传第一块");

            storePath = defaultAppendFileStorageClient.uploadAppenderFile("group1", qiwenMultipartFile.getUploadInputStream(),
                    qiwenMultipartFile.getSize(), qiwenMultipartFile.getExtendName());
            // 记录第一个分片上传的大小
            redisUtils.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size", qiwenMultipartFile.getSize(), 1000 * 60 * 60);

            log.info("第一块上传完成");
            if (storePath == null) {
                redisUtils.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number", uploadFile.getChunkNumber(), 1000 * 60 * 60);

                log.info("获取远程文件路径出错");
                throw new UploadException("获取远程文件路径出错");
            }

            redisUtils.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path", storePath.getPath(), 1000 * 60 * 60);

            log.info("上传文件 result = {}", storePath.getPath());
        } else {
            log.info("正在上传第{}块：" , uploadFile.getChunkNumber());

            String path = redisUtils.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");

            if (path == null) {
                log.error("无法获取已上传服务器文件地址");
                throw new UploadException("无法获取已上传服务器文件地址");
            }

            String uploadedSizeStr = redisUtils.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size");
            Long alreadySize = Long.parseLong(uploadedSizeStr);

            // 追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
            defaultAppendFileStorageClient.modifyFile("group1", path, qiwenMultipartFile.getUploadInputStream(),
                    qiwenMultipartFile.getSize(), alreadySize);
            // 记录分片上传的大小
            redisUtils.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size", alreadySize + qiwenMultipartFile.getSize(), 1000 * 60 * 60);

        }
    }

    @Override
    protected UploadFileResult organizationalResults(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();

        String path = redisUtils.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");
        uploadFileResult.setFileUrl(path);
        uploadFileResult.setFileName(qiwenMultipartFile.getFileName());
        uploadFileResult.setExtendName(qiwenMultipartFile.getExtendName());
        uploadFileResult.setFileSize(uploadFile.getTotalSize());
        if (uploadFile.getTotalChunks() == 1) {
            uploadFileResult.setFileSize(qiwenMultipartFile.getSize());
        }
        uploadFileResult.setStorageType(StorageTypeEnum.FAST_DFS);

        if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()) {
            log.info("分片上传完成");
            redisUtils.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number");
            redisUtils.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");
            redisUtils.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size");
            if (CusFileUtils.isImageFile(uploadFileResult.getExtendName())) {
                String group = "group1";
                String path1 = uploadFileResult.getFileUrl().substring(uploadFileResult.getFileUrl().indexOf("/") + 1);
                DownloadByteArray downloadByteArray = new DownloadByteArray();
                byte[] bytes = defaultAppendFileStorageClient.downloadFile(group, path1, downloadByteArray);
                InputStream is = new ByteArrayInputStream(bytes);

                BufferedImage src = null;
                try {
                    src = ImageIO.read(is);
                    uploadFileResult.setBufferedImage(src);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(is);
                }

            }
            uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
        } else {
            uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);
        }
        return uploadFileResult;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {
        // TODO
    }
}
