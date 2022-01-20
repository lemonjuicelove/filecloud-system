package com.github.jfcloud.jos.util;

import com.github.jfcloud.jos.entity.Fileinfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityReflectUitl2 {

    // private static List<String> classList = new ArrayList<>();
    private static List<Class> repeatList = new ArrayList<>(); // 避免出现属性的循环依赖

    /*static {
        classList.add("byte");
        classList.add("short");
        classList.add("int");
        classList.add("long");
        classList.add("float");
        classList.add("double");
        classList.add("boolean");
        classList.add("char");
        classList.add("Byte");
        classList.add("Short");
        classList.add("Integer");
        classList.add("Long");
        classList.add("Float");
        classList.add("Double");
        classList.add("Boolean");
        classList.add("Character");
        classList.add("Object");
        classList.add("String");
    }*/


    public static void main(String[] args) {
        String path = "D:\\IdeaProjects\\jfcloud-jos-k42\\jfcloud-jos-biz\\src\\main\\resources\\Fileinfo-message.txt";
        String codePre = "code.mode.";
        String pagePre1 = "page.placeholder.";
        String pagePre2 = "page.error.";
        reflectEntity(Fileinfo.class,path,codePre,pagePre1,pagePre2);
    }


    public static void reflectEntity(Class entity, String path, String codePre, String pagePre1, String pagePre2){

        if (entity == null || StringUtils.isEmpty(path)) throw new RuntimeException("实体类或路径错误");

        if (StringUtils.isEmpty(codePre) || StringUtils.isEmpty(pagePre1) || StringUtils.isEmpty(pagePre2)){
            throw new RuntimeException("前缀名错误");
        }

        if (repeatList.contains(entity)) return;
        repeatList.add(entity);

        // 获取类名的映射关系
        List<String> classInfo = getClassInfo(entity);

        // 获取父类的映射关系
        Map<String, List<String>> superInfo = getClassField(entity.getSuperclass(),path,codePre,pagePre1,pagePre2);

        // 获取自身的映射关系
        Map<String, List<String>> selfInfo = getClassField(entity,path,codePre,pagePre1,pagePre2);

        // 将编码提示写入文件
        writeFileCode(classInfo,superInfo,selfInfo,path,codePre);

        // 将页面相关提示写入文件
        writeFilePage(classInfo,superInfo,selfInfo,path,pagePre1,pagePre2);
    }


    // 写页面提示
    public static void writeFilePage(List<String> classInfo,
                                     Map<String, List<String>> superInfo,
                                     Map<String, List<String>> selfInfo,
                                     String path, String pagePre1, String pagePre2){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path,true);
            String clazzName = classInfo.get(0);
            clazzName = (char)(clazzName.charAt(0)+32) + clazzName.substring(1); // 将首字母大写变小写

            pagePre1 = pagePre1 + clazzName + ".";
            writeWithoutBlank(fos,superInfo,pagePre1);
            writeWithoutBlank(fos,selfInfo,pagePre1);

            fos.write('\n');

            pagePre2 = pagePre2 + clazzName + ".";
            writeWithoutBlank(fos,superInfo,pagePre2);
            writeWithoutBlank(fos,selfInfo,pagePre2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 写编码提示
    public static void writeFileCode(List<String> classInfo,
                                 Map<String, List<String>> superInfo,
                                 Map<String, List<String>> selfInfo,
                                 String path, String codePre){
        // 将信息写入文件
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            String clazzName = classInfo.get(0);
            clazzName = (char)(clazzName.charAt(0)+32) + clazzName.substring(1); // 将首字母大写变小写

            String first = codePre + clazzName + "=" + classInfo.get(1) + "\n";
            fos.write(first.getBytes());

            codePre = codePre + clazzName + ".";
            writeWithBlank(fos,superInfo,codePre);
            writeWithBlank(fos,selfInfo,codePre);

            fos.write('\n');
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 写code
    public static void writeWithBlank(FileOutputStream fos,Map<String, List<String>> info, String pre) throws IOException {
        if (fos != null && info != null){
            for (Map.Entry<String, List<String>> entry : info.entrySet()) {
                String msg = pre + entry.getKey() + "=" + entry.getValue().get(0) + "\n";
                fos.write(msg.getBytes());
                // 写blank提示
                if ("true".equals(entry.getValue().get(1))){
                    String blank = pre + entry.getKey() + ".blank=请填写" + entry.getValue().get(0) + "! \n";
                    fos.write(blank.getBytes());
                }
            }
        }
    }


    // 写page
    public static void writeWithoutBlank(FileOutputStream fos,Map<String, List<String>> info, String pre) throws IOException {
        if (fos != null && info != null){
            for (Map.Entry<String, List<String>> entry : info.entrySet()) {
                String msg = pre + entry.getKey() + "=" + entry.getValue().get(0) + "\n";
                fos.write(msg.getBytes());
            }
        }
    }


    // 获取类名和注解的映射信息
    public static List<String> getClassInfo(Class clazz){

        if (clazz == Object.class) return null;

        List<String> info = new ArrayList<>();
        ApiModel annotation = (ApiModel) clazz.getAnnotation(ApiModel.class);
        if (annotation == null) throw new RuntimeException(clazz.getSimpleName() + "类缺少注解修饰");

        info.add(clazz.getSimpleName());
        info.add(annotation.value());

        return info;
    }


    // 获取类中的所有属性与注解的映射信息
    public static Map<String, List<String>> getClassField(Class clazz, String path,
                                                          String codePre, String pagePre1, String pagePre2){

        // 是Object类，跳过
        if (clazz == Object.class) return null;

        Map<String,List<String>> info = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) continue;

            ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
            if (annotation == null) throw new RuntimeException(clazz.getSimpleName() + "类的" + field.getName() + "属性缺少注解修饰");

            List<String> value = new ArrayList<>();
            value.add(annotation.value());
            value.add(annotation.required() + "");
            info.put(field.getName(),value);

            Class type = field.getType();
            // 解决引用类型的属性
            if (/*!classList.contains(type.getSimpleName()) && */type.getAnnotation(ApiModel.class) != null){
                String clazzName = clazz.getSimpleName();
                int pos = path.lastIndexOf(".");
                path = path.substring(0,pos) + "-" + field.getName() + path.substring(pos);
                clazzName = (char)(clazzName.charAt(0)+32) + clazzName.substring(1); // 将首字母大写变小写
                codePre = codePre + clazzName + ".";
                pagePre1 = pagePre1 + clazzName + ".";
                pagePre2 = pagePre2 + clazzName + ".";
                reflectEntity(type,path,codePre,pagePre1,pagePre2);
            }
        }

        return info;
    }

}
