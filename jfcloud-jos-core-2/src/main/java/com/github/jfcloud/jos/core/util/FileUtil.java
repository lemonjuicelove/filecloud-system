package com.github.jfcloud.jos.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    // 根据后缀名判断文件类型
    public static String getMine(String filename){
        Path path = Paths.get(filename);
        String type = null;
        try {
            type = Files.probeContentType(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return type;
    }

    // 根据文件名获取前缀
    public static String getPre(String filename){
        return filename.substring(0,filename.indexOf("."));
    }

    // 根据文件名获取后缀
    public static String getPro(String filename){
        return filename.substring(filename.indexOf(".")+1);
    }

}
