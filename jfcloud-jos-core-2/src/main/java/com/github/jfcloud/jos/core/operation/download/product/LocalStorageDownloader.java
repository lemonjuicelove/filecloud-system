package com.github.jfcloud.jos.core.operation.download.product;

import com.github.jfcloud.jos.core.exception.operation.DownloadException;
import com.github.jfcloud.jos.core.operation.download.Downloader;
import com.github.jfcloud.jos.core.operation.download.entity.DownloadFile;
import com.github.jfcloud.jos.core.util.DateUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
    本地存储实现类：文件下载
 */
@Component
public class LocalStorageDownloader implements Downloader {

    // 文件下载
    @Override
    public void download(DownloadFile downloadFile, HttpServletRequest request, HttpServletResponse response) {

        OutputStream os = null;
        FileInputStream input = null;
        String filename = downloadFile.getFilename();
        try {
            // 设置返回客户端浏览器，解决文件名乱码问题
            String agent = request.getHeader("USER-AGENT");
            try{
                // 针对以ie为内核的浏览器
                if (agent.contains("MSIE") || agent.contains("Trident")){
                    filename = java.net.URLEncoder.encode(filename,"UTF-8");
                }else{
                    // 非ie浏览器的处理
                    filename = new String(filename.getBytes("UTF-8"),"ISO-8859-1");
                }
            }catch (Exception e){
                e.printStackTrace();
                throw new DownloadException("文件下载错误");
            }
            response.setContentType("PPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);

            File f = new File(downloadFile.getPath());
            input = new FileInputStream(f);
            os = response.getOutputStream();

            byte[] buffer  = new byte[1024*1024];

            if (input.available() == 0){ // 空文件
                os.write(buffer,0,0);
            }else{ // 非空文件
                int numRead = 0;
                while((numRead = input.read(buffer)) != -1){
                    os.write(buffer,0,numRead);
                }
            }

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

    @Override
    public InputStream InputStream(DownloadFile downloadFile) {
        File f = new File(downloadFile.getPath());
        InputStream input = null;
        try {
            input = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return input;
    }



    // 批量文件下载
    // @Override
    /*public void downloadBatch(List<DownloadFile> downloadFiles, HttpServletRequest request, HttpServletResponse response) {

        response.reset();
        // 设置响应的编码方式
        response.setCharacterEncoding("utf-8");
        // 设置响应的内容类型
        response.setContentType("PPLICATION/OCTET-STREAM");

        // 设置压缩包的名字
        String dates = DateUtil.formatDate(new Date());
        String billname = "附件包-" + dates;
        String downloadName = billname + ".zip";

        // 设置返回客户端浏览器，解决文件名乱码问题
        String agent = request.getHeader("USER-AGENT");
        try{
            // 针对以ie为内核的浏览器
            if (agent.contains("MSIE") || agent.contains("Trident")){
                downloadName = java.net.URLEncoder.encode(downloadName,"UTF-8");
            }else{
                // 非ie浏览器的处理
                downloadName = new String(downloadName.getBytes("UTF-8"),"ISO-8859-1");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // 设置响应的文件的名字和类型
        response.setHeader("Content-Disposition","attachment;filename=" + downloadName);

        // 设置压缩流：边压缩边下载
        ZipOutputStream zipos = null;
        try {
            zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            zipos.setMethod(ZipOutputStream.DEFLATED);
        }catch (Exception e){
            e.printStackTrace();
        }

        // 将文件写入压缩流
        DataOutputStream dos = null;

        for (DownloadFile downloadFile : downloadFiles) {
            File file = new File(downloadFile.getPath());
            if (!file.exists()){
                throw new DownloadException("文件不存在");
            }else{
                FileInputStream fis = null;
                try{
                    zipos.putNextEntry(new ZipEntry(downloadFile.getFilename()));
                    dos = new DataOutputStream(zipos);
                    fis = new FileInputStream(file);
                    byte[] buffer = new byte[1024*1024];
                    int readNum = 0;
                    while ((readNum = fis.read(buffer)) != -1){
                        dos.write(buffer,0,readNum);
                    }
                    dos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (fis != null){
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (dos != null){
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (zipos != null){
            try {
                zipos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

}
