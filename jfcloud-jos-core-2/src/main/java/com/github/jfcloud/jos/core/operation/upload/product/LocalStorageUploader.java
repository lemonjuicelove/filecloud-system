package com.github.jfcloud.jos.core.operation.upload.product;

import com.github.jfcloud.jos.core.common.StorageTypeEnum;
import com.github.jfcloud.jos.core.common.UploadFileStatusEnum;
import com.github.jfcloud.jos.core.config.LocalConfig;
import com.github.jfcloud.jos.core.exception.operation.UploadException;
import com.github.jfcloud.jos.core.operation.upload.Uploader;
import com.github.jfcloud.jos.core.operation.upload.entity.UploadFile;
import com.github.jfcloud.jos.core.operation.upload.entity.UploadFileResults;
import com.github.jfcloud.jos.core.util.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;

/*
    本地存储实现类：文件上传
 */
public class LocalStorageUploader implements Uploader {

    private LocalConfig localConfig;

    public LocalStorageUploader() {
    }

    public LocalStorageUploader(LocalConfig localConfig) {
        this.localConfig = localConfig;
    }


    // 上传切片文件
    @Override
    public UploadFileResults upload(UploadFile uploadFile) {

        MultipartFile file = uploadFile.getFile();
        String pre = FileUtil.getPre(uploadFile.getFileName());
        String dirname = pre + "-" + uploadFile.getWholeIdentifier();
        String filename = uploadFile.getChunkNumber() + "-" + uploadFile.getIdentifier();

        if (file.equals("") || file.getSize() <= 0) {
            throw new UploadException("上传文件出现异常");
        }
        InputStream fis = null;
        FileOutputStream fos = null;
        try{
            fis = file.getInputStream();
            File dir = new File(localConfig.getTempPath() + "\\" + dirname);
            if (!dir.exists()){
                dir.mkdir();
            }
            fos = new FileOutputStream(dir.getAbsolutePath() + "\\" + filename);
            int bytesRead = 0;
            byte[] buffer = new byte[1024*1024];
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
        }catch (Exception e){
            e.printStackTrace();
            throw new UploadException("上传文件出现异常");
        }finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        UploadFileResults results = new UploadFileResults();
        results.setFileName(uploadFile.getWholeIdentifier());
        results.setStorageType(StorageTypeEnum.LOCAL);

        if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()){ // 说明是最后一片切片，合并切片
            File mergeFile = mergeFile(uploadFile.getFileName(), uploadFile.getWholeIdentifier());
            results.setFileSize(mergeFile.length());
            results.setFileUrl(mergeFile.getAbsolutePath());
            results.setStatus(UploadFileStatusEnum.SUCCESS);
        }else{ // 不是最后一片切片
            results.setStatus(UploadFileStatusEnum.UNCOMPLATE);
        }

        return results;
    }

    // 合并切片文件
    private File mergeFile(String filename,String metadata){

        String pre = FileUtil.getPre(filename);
        File file = new File(localConfig.getTempPath() + "\\" + pre + "-" + metadata);
        if (!file.exists()) return null;

        // 将切片按顺序排列
        File[] files = file.listFiles();
        Arrays.sort(files, (t1, t2) -> {
            String name1 = t1.getName();
            Integer index1 = Integer.valueOf(name1.substring(0, name1.indexOf("-")));

            String name2 = t2.getName();
            Integer index2 = Integer.valueOf(name2.substring(0, name2.indexOf("-")));
            return index1 - index2;
        });

        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fos = new FileOutputStream(localConfig.getRealPath() + "\\" + metadata,true);
            for (File temp : files) {
                fis = new FileInputStream(temp);
                byte[] bytes = new byte[1024*1024];
                int readNum;
                while ((readNum = fis.read(bytes)) != -1){
                    fos.write(bytes,0,readNum);
                }
                fos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UploadException("上传文件出现了异常");
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        deleteTempFile(filename,metadata);

        return new File(localConfig.getRealPath() + "\\" + metadata);
    }

    // 删除临时文件
    private void deleteTempFile(String filename,String metadata) {
        String pre = FileUtil.getPre(filename);
        File file = new File(localConfig.getTempPath() + "\\" + pre + "-" + metadata);
        if (file.exists()){
            file.delete();
        }
    }

}
