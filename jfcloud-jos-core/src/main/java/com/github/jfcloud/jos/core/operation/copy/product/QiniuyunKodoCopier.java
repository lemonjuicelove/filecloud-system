package com.github.jfcloud.jos.core.operation.copy.product;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import com.github.jfcloud.jos.core.config.QiniuyunConfig;
import com.github.jfcloud.jos.core.operation.copy.Copier;
import com.github.jfcloud.jos.core.operation.copy.domain.CopyFile;
import com.github.jfcloud.jos.core.util.QiniuyunUtils;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
public class QiniuyunKodoCopier extends Copier {

    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoCopier(){

    }

    public QiniuyunKodoCopier(QiniuyunConfig qiniuyunConfig) {
        this.qiniuyunConfig = qiniuyunConfig;
    }
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = CusFileUtils.getUploadFileUrl(uuid, copyFile.getExtendName());

        qiniuUpload(fileUrl, inputStream);

        return fileUrl;
    }

    private void qiniuUpload(String fileUrl, InputStream inputStream) {
        Configuration cfg = QiniuyunUtils.getCfg(qiniuyunConfig);


        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());
        String upToken = auth.uploadToken(qiniuyunConfig.getKodo().getBucketName());

        String localTempDir = CusFileUtils.getStaticPath() + "temp";


        try {
            //设置断点续传文件进度保存目录
            FileRecorder fileRecorder = new FileRecorder(localTempDir);
            UploadManager uploadManager = new UploadManager(cfg, fileRecorder);
            Response response = uploadManager.put(inputStream, fileUrl, upToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            log.info(putRet.key);
            log.info(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }



    }


}
