package com.github.jfcloud.jos.core.operation;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zj
 * @date 2021/12/31
 */
public class ImageOperation {
    private static final Logger log = LoggerFactory.getLogger(ImageOperation.class);

    public ImageOperation() {
    }

    public static void leftTotation(File inFile, File outFile, int angle) throws IOException {
        Thumbnails.of(new File[]{inFile}).scale(1.0D).outputQuality(1.0F).rotate((double)(-angle)).toFile(outFile);
    }

    public static void rightTotation(File inFile, File outFile, int angle) throws IOException {
        Thumbnails.of(new File[]{inFile}).scale(1.0D).outputQuality(1.0F).rotate((double)angle).toFile(outFile);
    }

    public static void thumbnailsImage(File inFile, File outFile, int width, int height) throws IOException {
        Thumbnails.of(new File[]{inFile}).size(width, height).toFile(outFile);
    }

    public static InputStream thumbnailsImage(InputStream inputStream, File outFile, int width, int height) throws IOException {
        File parentFile = outFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        BufferedImage bufferedImage = ImageIO.read(inputStream);
        int oriHeight = bufferedImage.getHeight();
        int oriWidth = bufferedImage.getWidth();
        if (oriHeight > height && oriWidth > width) {
            if (oriHeight < oriWidth) {
                Thumbnails.of(new BufferedImage[]{bufferedImage}).outputQuality(1.0F).scale(1.0D).sourceRegion(Positions.CENTER, oriHeight, oriHeight).toFile(outFile);
            } else {
                Thumbnails.of(new BufferedImage[]{bufferedImage}).outputQuality(1.0F).scale(1.0D).sourceRegion(Positions.CENTER, oriWidth, oriWidth).toFile(outFile);
            }

            Thumbnails.of(new BufferedImage[]{ImageIO.read(outFile)}).outputQuality(0.9D).size(width, height).toFile(outFile);
        } else {
            ImageIO.write(bufferedImage, FilenameUtils.getExtension(outFile.getName()), outFile);
        }

        return new FileInputStream(outFile);
    }

    public static String getFileExtendName(String fileName) {
        return fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
