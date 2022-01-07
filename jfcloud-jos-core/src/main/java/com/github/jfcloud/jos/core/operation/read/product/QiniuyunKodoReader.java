package com.github.jfcloud.jos.core.operation.read.product;

import com.github.jfcloud.jos.core.util.HttpsUtils;
import com.qiniu.util.Auth;
import com.github.jfcloud.jos.core.config.QiniuyunConfig;
import com.github.jfcloud.jos.core.exception.operation.ReadException;
import com.github.jfcloud.jos.core.operation.read.Reader;
import com.github.jfcloud.jos.core.operation.read.domain.ReadFile;
import com.github.jfcloud.jos.core.util.ReadFileUtils;
import com.github.jfcloud.jos.core.util.CusFileUtils;

import java.io.IOException;
import java.io.InputStream;

public class QiniuyunKodoReader extends Reader {

    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoReader(){

    }

    public QiniuyunKodoReader(QiniuyunConfig qiniuyunConfig) {
        this.qiniuyunConfig = qiniuyunConfig;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = CusFileUtils.getFileExtendName(fileUrl);
        try {
            return ReadFileUtils.getContentByInputStream(fileType, getInputStream(readFile.getFileUrl()));
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        }
    }

    public InputStream getInputStream(String fileUrl) {
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());

        String urlString = auth.privateDownloadUrl(qiniuyunConfig.getKodo().getDomain() + "/" + fileUrl);



        return HttpsUtils.doGet(urlString);
    }


}
