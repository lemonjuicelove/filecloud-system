package com.github.jfcloud.jos.core.operation.preview;

import com.github.jfcloud.jos.core.operation.ImageOperation;
import com.github.jfcloud.jos.core.operation.VideoOperation;
import com.github.jfcloud.jos.core.domain.ThumbImage;
import com.github.jfcloud.jos.core.operation.preview.domain.PreviewFile;
import com.github.jfcloud.jos.core.util.CharsetUtils;
import com.github.jfcloud.jos.core.util.CusFileUtils;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Data
public abstract class Previewer {

    public ThumbImage thumbImage;

    protected abstract InputStream getInputStream(PreviewFile previewFile);

    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        String fileUrl = previewFile.getFileUrl();
        boolean isVideo = CusFileUtils.isVideoFile(CusFileUtils.getFileExtendName(fileUrl));
        String thumbnailImgUrl = previewFile.getFileUrl();
        if (isVideo) {
            thumbnailImgUrl = fileUrl.replace("." + CusFileUtils.getFileExtendName(fileUrl), ".jpg");
        }


        File saveFile = CusFileUtils.getCacheFile(thumbnailImgUrl);

        if (saveFile.exists()) {
            FileInputStream fis = null;
            OutputStream outputStream = null;
            try {
                fis = new FileInputStream(saveFile);
                outputStream = httpServletResponse.getOutputStream();
                IOUtils.copy(fis, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fis);
                IOUtils.closeQuietly(outputStream);
            }

        } else {
            InputStream inputstream = null;
            OutputStream outputStream = null;
            InputStream in = null;
            try {
                inputstream = getInputStream(previewFile);
                outputStream = httpServletResponse.getOutputStream();
                int thumbImageWidth = thumbImage.getWidth();
                int thumbImageHeight = thumbImage.getHeight();
                int width = thumbImageWidth == 0 ? 150 : thumbImageWidth;
                int height = thumbImageHeight == 0 ? 150 : thumbImageHeight;

                if (isVideo) {
                    in = VideoOperation.thumbnailsImage(inputstream, saveFile, width, height);
                } else {
                    in = ImageOperation.thumbnailsImage(inputstream, saveFile, width, height);
                }
                IOUtils.copy(in, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(outputStream);
                if (previewFile.getOssClient() != null) {
                    previewFile.getOssClient().shutdown();
                }
            }


        }
    }

    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        InputStream inputStream = null;

        OutputStream outputStream = null;

        try {
            inputStream = getInputStream(previewFile);
            outputStream = httpServletResponse.getOutputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            bytes = CharsetUtils.convertTxtCharsetToUTF8(bytes, CusFileUtils.getFileExtendName(previewFile.getFileUrl()));
            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            if (previewFile.getOssClient() != null) {
                previewFile.getOssClient().shutdown();
            }
        }
    }


}
