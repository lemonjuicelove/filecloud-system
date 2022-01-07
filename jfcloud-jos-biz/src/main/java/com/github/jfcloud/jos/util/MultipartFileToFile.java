package com.github.jfcloud.jos.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/*
    MultipartFile文件和File文件的相关工具类
 */
public class MultipartFileToFile {

    /*public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream("C:\\Users\\73561\\Desktop\\bioradar\\filecloud\\"+file.getName());
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    *//**
     * 删除本地临时文件
     * @param
     *//*
    public static void delteTempFile(File file) {
        if (file != null) {
            File del = new File(file.toURI());
            del.delete();
        }
    }*/

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

    // 断点上传
    public static File uploadFileProcess(MultipartFile multipartFile, String fileName) throws IOException {

        Map<Object, Object> cacheMap = InitialCache.getCacheMap();
        File file = null;
        // 空文件直接返回空
        if("".equals(multipartFile)){
            file = null;
        }else{
            String realSavePath = (String) cacheMap.get("local_real");
            String tempSavePath = (String) cacheMap.get("local_temp");
            File tempFile = new File(tempSavePath + fileName);
            File realFile = new File(realSavePath + fileName);

            if (realFile.exists()){
                System.out.println("文件已经存在，请不要重复上传");
                return realFile;
            }else{
                InputStream in = multipartFile.getInputStream();
                long needSkipBytes = 0;
                if (tempFile.exists()){ // 续传
                    needSkipBytes = tempFile.length();
                }else{
                    tempFile.createNewFile();
                }
                in.skip(needSkipBytes);
                RandomAccessFile tempRandAccessFile = new RandomAccessFile(tempFile,"rw");
                tempRandAccessFile.seek(needSkipBytes);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) > 0){
                    tempRandAccessFile.write(buffer);
                }
                in.close();
                tempRandAccessFile.close();
                realFile.createNewFile();
                if (MultipartFileToFile.fileCopy(tempFile,realFile)){
                    tempFile.delete();
                }
            }
            file = realFile;
        }

        return file;
    }

    public static boolean fileCopy(File source, File target){
        boolean success = true;

        try{
            FileInputStream in = new FileInputStream(source);
            FileOutputStream out = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) > 0){
                out.write(buffer);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

}
