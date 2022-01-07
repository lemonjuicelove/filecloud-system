package com.github.jfcloud.jos.core.operation;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author zj
 * @date 2021/12/31
 */
public class VideoOperation {
    private static final Logger log = LoggerFactory.getLogger(VideoOperation.class);

    public VideoOperation() {
    }

    public static InputStream thumbnailsImage(InputStream inputStream, File outFile, int width, int height) throws IOException {
        try {
            FFmpegFrameGrabber ff = new FFmpegFrameGrabber(inputStream);
            ff.start();
            int videoLength = ff.getLengthInFrames();
            Frame f = null;

            for(int i = 0; i < videoLength; ++i) {
                f = ff.grabFrame();
                if (i > 20 && f.image != null) {
                    break;
                }
            }

            int owidth = f.imageWidth;
            int oheight = f.imageHeight;
            height = (int)((double)width / (double)owidth * (double)oheight);
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage fecthedImage = converter.getBufferedImage(f);
            BufferedImage bi = new BufferedImage(width, height, 5);
            bi.getGraphics().drawImage(fecthedImage.getScaledInstance(width, height, 4), 0, 0, (ImageObserver)null);
            File saveDir = outFile.getParentFile().getAbsoluteFile();
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            ImageIO.write(bi, "jpg", outFile);
            ff.stop();
        } catch (IOException var14) {
            var14.printStackTrace();
        } catch (Exception var15) {
            String errorMessage = var15.getMessage();
            if (errorMessage.contains("AWTError")) {
                log.info(var15.getMessage());
            }

            log.error(var15.getMessage());
        }

        return new FileInputStream(outFile);
    }
}
