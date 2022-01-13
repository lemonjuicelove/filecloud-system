package com.github.jfcloud.jos.util;


import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/*
    MultipartFile文件和File文件的相关工具类
 */
public class UploadUtil {

    public static Map cacheMap;
    public static String realSavePath;
    public static String tempSavePath;

    public static final String EMPTYMD5 = "d41d8cd98f00b204e9800998ecf8427e";
    public static final String EMPTYSHA1 = "da39a3ee5e6b4b0d3255bfef95601890afd80709";
    public static final String EMPTYCRC32 = "0";
    public static final String EMPTYFILEPATH = "C:\\Users\\73561\\Desktop\\bioradar\\real\\d41d8cd98f00b204e9800998ecf8427e";

    static {
        cacheMap = InitialCache.getCacheMap();
        realSavePath = (String) InitialCache.getCacheMap().get("local_real");
        tempSavePath = (String) InitialCache.getCacheMap().get("local_temp");
    }

    // 新建空文件
    /*public static File createFile(String filename){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(realSavePath + filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new File(realSavePath + filename);
    }*/

    // 上传切片文件
    public static boolean uploadFile(MultipartFile file,String dirname,String filename){
        if (file.equals("") || file.getSize() <= 0) {
            return false;
        }
        InputStream fis = null;
        FileOutputStream fos = null;
        try{
            fis = file.getInputStream();
            File dir = new File(tempSavePath + dirname);
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
            return false;
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

        return true;
    }

    // 合并切片
    public static File mergeFile(String filename,String metadata){

        String pre = getPre(filename);
        File file = new File(tempSavePath + pre + "-" + metadata);
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
            fos = new FileOutputStream(realSavePath + metadata,true);
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
            return null;
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

        return new File(realSavePath + metadata);
    }

    // 删除临时文件
    public static void deleteTempFile(String filename,String metadata) {
        String pre = getPre(filename);
        File file = new File(tempSavePath + pre + "-" + metadata);
        if (file.exists()){
            file.delete();
        }
    }

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

    // 下载文件
    public static void downloadFile(String filename, String path, HttpServletResponse response){

        OutputStream os = null;
        FileInputStream input = null;
        try {
            File f = new File(path);
            input = new FileInputStream(f);
            byte[] buffer  = new byte[(int)f.length()];
            int offset = 0;
            int numRead = 0;
            // 可以写空文件
            while (offset<buffer.length&&(numRead-input.read(buffer,offset,buffer.length-offset))>=0) {
                offset+=numRead;
            }
            // 只能写非空文件
            /*byte[] buffer  = new byte[1024*1024];
            int readNum = 0;
            while ((readNum = input.read(buffer)) != -1){
                os.write(buffer,0,readNum);
            }
            os.flush();*/
            os = response.getOutputStream();
            response.setContentType(DownloadConstant.CONTENTTYPE);
            response.setHeader(DownloadConstant.HEADNAME, DownloadConstant.HEADVALUE + URLEncoder.encode(filename, DownloadConstant.HEADENCODE));
            os.write(buffer);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 断点上传
    /*public static File uploadFileProcess(MultipartFile multipartFile, String fileName) throws IOException {

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
    }*/

    /*public static boolean fileCopy(File source, File target){
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
    }*/

}
