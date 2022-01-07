package com.github.jfcloud.jos.core.operation;

import com.alibaba.fastjson.JSON;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zj
 * @date 2021/12/31
 */
public class FileOperation {
    private static final Logger log = LoggerFactory.getLogger(FileOperation.class);
    private static Executor executor = Executors.newFixedThreadPool(20);

    public FileOperation() {
    }

    public static File newFile(String fileUrl) {
        File file = new File(fileUrl);
        return file;
    }

    public static boolean deleteFile(File file) {
        if (file == null) {
            return false;
        } else if (!file.exists()) {
            return false;
        } else if (file.isFile()) {
            return file.delete();
        } else {
            File[] var1 = file.listFiles();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                File newfile = var1[var3];
                deleteFile(newfile);
            }

            return file.delete();
        }
    }

    public static boolean deleteFile(String fileUrl) {
        File file = newFile(fileUrl);
        return deleteFile(file);
    }

    public static long getFileSize(String fileUrl) {
        File file = newFile(fileUrl);
        return file.exists() ? file.length() : 0L;
    }

    public static long getFileSize(File file) {
        return file == null ? 0L : file.length();
    }

    public static boolean mkdir(File file) {
        if (file == null) {
            return false;
        } else {
            return file.exists() ? true : file.mkdirs();
        }
    }

    public static boolean mkdir(String fileUrl) {
        if (fileUrl == null) {
            return false;
        } else {
            File file = newFile(fileUrl);
            return file.exists() ? true : file.mkdirs();
        }
    }

    public static void copyFile(FileInputStream fileInputStream, FileOutputStream fileOutputStream) throws IOException {
        try {
            byte[] buf = new byte[4096];

            for(int len = fileInputStream.read(buf); len != -1; len = fileInputStream.read(buf)) {
                fileOutputStream.write(buf, 0, len);
            }
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }

        }

    }

    public static void copyFile(File src, File dest) throws IOException {
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dest);
        copyFile(in, out);
    }

    public static void copyFile(String srcUrl, String destUrl) throws IOException {
        if (srcUrl != null && destUrl != null) {
            File srcFile = newFile(srcUrl);
            File descFile = newFile(destUrl);
            copyFile(srcFile, descFile);
        }
    }

    public static List<String> unzip(File sourceFile, String destDirPath) {
        ZipFile zipFile = null;
        Set<String> set = new HashSet();
        List<String> fileEntryNameList = new ArrayList();
        Enumeration entries = null;

        try {
            try {
                ZipFile tempZipFile = new ZipFile(sourceFile);
                entries = tempZipFile.entries();
                log.info(JSON.toJSONString(entries));
                zipFile = new ZipFile(sourceFile);
                entries = zipFile.entries();
            } catch (IOException var48) {
                throw new RuntimeException("unzip error from ZipUtils", var48);
            } catch (IllegalArgumentException var49) {
                try {
                    zipFile = new ZipFile(sourceFile, Charset.forName("GBK"));
                    entries = zipFile.entries();
                } catch (IOException var47) {
                    var47.printStackTrace();
                }
            } catch (Exception var50) {
                throw new RuntimeException("unzip error from ZipUtils", var50);
            }

            label312:
            while(true) {
                while(true) {
                    if (!entries.hasMoreElements()) {
                        break label312;
                    }

                    ZipEntry entry = (ZipEntry)entries.nextElement();
                    String[] nameStrArr = entry.getName().split("/");
                    String nameStr = "";

                    for(int i = 0; i < nameStrArr.length; ++i) {
                        if (!"".equals(nameStrArr[i])) {
                            nameStr = nameStr + "/" + nameStrArr[i];
                            set.add(nameStr);
                        }
                    }

                    log.info("解压" + entry.getName());
                    String zipPath = "/" + entry.getName();
                    fileEntryNameList.add(zipPath);
                    if (entry.isDirectory()) {
                        String dirPath = destDirPath + File.separator + entry.getName();
                        File dir = newFile(dirPath);
                        dir.mkdir();
                    } else {
                        File targetFile = new File(destDirPath + "/" + entry.getName());
                        if (!targetFile.getParentFile().exists()) {
                            targetFile.getParentFile().mkdirs();
                        }

                        InputStream is = null;
                        FileOutputStream fos = null;

                        try {
                            targetFile.createNewFile();
                            is = zipFile.getInputStream(entry);
                            fos = new FileOutputStream(targetFile);
                            byte[] buf = new byte[2048];

                            int len;
                            while((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                            }
                        } catch (Exception var51) {
                            throw new RuntimeException("解压过程失败", var51);
                        } finally {
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (Exception var46) {
                                    log.error("关闭流失败:" + var46);
                                }
                            }

                            if (is != null) {
                                try {
                                    is.close();
                                } catch (Exception var45) {
                                    log.error("关闭流失败：" + var45);
                                }
                            }

                        }
                    }
                }
            }
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException var44) {
                    log.error("关闭流失败：{}", var44.getMessage());
                }
            }

        }

        List<String> res = new ArrayList(set);
        return res;
    }

    public static List<String> unrar(File sourceFile, String destDirPath) throws Exception {
        File destDir = new File(destDirPath);
        Set<String> set = new HashSet();
        Archive archive = null;
        FileOutputStream fos = null;
        System.out.println("Starting 开始解压...");

        try {
            archive = new Archive(sourceFile);
            FileHeader fh = archive.nextFileHeader();
            int count = 0;
            File destFileName = null;

            while(true) {
                if (fh == null) {
                    archive.close();
                    archive = null;
                    System.out.println("Finished 解压完成!");
                    break;
                }

                set.add("/" + fh.getFileName());
                PrintStream var10000 = System.out;
                StringBuilder var10001 = new StringBuilder();
                ++count;
                var10000.println(var10001.append(count).append(") ").append(fh.getFileName()).toString());
                String compressFileName = fh.getFileName().trim();
                destFileName = new File(destDir.getAbsolutePath() + "/" + compressFileName);
                if (fh.isDirectory()) {
                    if (!destFileName.exists()) {
                        destFileName.mkdirs();
                    }

                    fh = archive.nextFileHeader();
                } else {
                    if (!destFileName.getParentFile().exists()) {
                        destFileName.getParentFile().mkdirs();
                    }

                    fos = new FileOutputStream(destFileName);
                    archive.extractFile(fh, fos);
                    fos.close();
                    fos = null;
                    fh = archive.nextFileHeader();
                }
            }
        } catch (Exception var20) {
            throw var20;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception var19) {
                    log.error("关闭流失败：" + var19.getMessage());
                }
            }

            if (archive != null) {
                try {
                    archive.close();
                } catch (Exception var18) {
                    log.error("关闭流失败：" + var18.getMessage());
                }
            }

        }

        List<String> res = new ArrayList(set);
        return res;
    }

    public static void saveDataToFile(String filePath, String fileName, String data) {
        BufferedWriter writer = null;
        new File(filePath);
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }

        File file = new File(filePath + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException var18) {
                var18.printStackTrace();
            }
        }

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            writer.write(data);
        } catch (IOException var16) {
            var16.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException var15) {
                var15.printStackTrace();
            }

        }

        System.out.println("文件写入成功！");
    }
}
