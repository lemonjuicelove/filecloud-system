package com.github.jfcloud.jos.core.operation.preview.product;

import com.github.jfcloud.jos.core.domain.ThumbImage;
import com.github.jfcloud.jos.core.operation.preview.Previewer;
import com.github.jfcloud.jos.core.operation.preview.domain.PreviewFile;
import com.github.jfcloud.jos.core.util.CusFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LocalStoragePreviewer extends Previewer {

    public LocalStoragePreviewer(){

    }
    public LocalStoragePreviewer(ThumbImage thumbImage) {
        setThumbImage(thumbImage);
    }

    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        //设置文件路径
        File file = CusFileUtils.getLocalSaveFile(previewFile.getFileUrl());
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return inputStream;

    }

}
