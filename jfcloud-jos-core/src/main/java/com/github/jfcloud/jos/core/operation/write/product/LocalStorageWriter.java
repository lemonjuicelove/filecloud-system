package com.github.jfcloud.jos.core.operation.write.product;

import com.github.jfcloud.jos.core.exception.operation.WriteException;
import com.github.jfcloud.jos.core.operation.write.Writer;
import com.github.jfcloud.jos.core.operation.write.domain.WriteFile;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class LocalStorageWriter extends Writer {
    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        try (FileOutputStream out = new FileOutputStream(CusFileUtils.getStaticPath() + writeFile.getFileUrl())){
            int read;
            final byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            throw new WriteException("待写入的文件不存在:{}", e);
        } catch (IOException e) {
            throw new WriteException("IO异常:{}", e);
        }
    }
}
