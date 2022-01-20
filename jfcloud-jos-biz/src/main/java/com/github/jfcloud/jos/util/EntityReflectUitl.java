package com.github.jfcloud.jos.util;

import com.github.jfcloud.jos.entity.FileShare;
import com.github.jfcloud.jos.entity.Fileinfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityReflectUitl {

    public static void main(String[] args) {
        reflectEntity(Fileinfo.class);
    }

    public static void reflectEntity(Class entity){

        if (entity == null) return;

        // 获取类名的映射关系
        List<String> classInfo = getClassInfo(entity);

        // 获取父类的映射关系
        Map<String, List<String>> superInfo = getClassField(entity.getSuperclass());

        // 获取自身的映射关系
        Map<String, List<String>> selfInfo = getClassField(entity);


        // 将编码提示写入文件
        String path = "D:\\IdeaProjects\\jfcloud-jos-k42\\jfcloud-jos-biz\\src\\main\\resources\\Fileinfo-message.txt";
        writeFileCode(classInfo,superInfo,selfInfo,path);

        // 将页面相关提示写入文件
        writeFilePage(classInfo,superInfo,selfInfo,path);
    }

    // 写页面提示
    public static void writeFilePage(List<String> classInfo,
                                     Map<String, List<String>> superInfo,
                                     Map<String, List<String>> selfInfo,
                                     String path){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path,true);
            // 写页面相关提示
            String first = "page: # 页面相关提示 \n";
            fos.write(first.getBytes());
            for (int i = 0; i < 1; i++) {
                fos.write('\t');
            }
            // 写页面注释相关提示
            String second = "placeholder: # 页面注释相关提示 \n";
            fos.write(second.getBytes());
            for (int i = 0; i < 2; i++) {
                fos.write('\t');
            }
            // 写类名信息
            String clazz = classInfo.get(0) + ": # 实体 \n";
            fos.write(clazz.getBytes());
            for (int i = 0; i < 3; i++) {
                fos.write('\t');
            }
            // 写实体属性
            if (superInfo != null){
                for (Map.Entry<String, List<String>> entry : superInfo.entrySet()) {
                    String msg = entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                    fos.write(msg.getBytes());
                    for (int i = 0; i < 3; i++) {
                        fos.write('\t');
                    }
                }
            }
            if (selfInfo != null){
                for (Map.Entry<String, List<String>> entry : selfInfo.entrySet()) {
                    String msg = entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                    fos.write(msg.getBytes());
                    for (int i = 0; i < 3; i++) {
                        fos.write('\t');
                    }
                }
            }

            fos.write('\n');
            fos.write('\t');
            // 写校验相关提示
            String third = "error: # 校验相关提示 \n";
            fos.write(third.getBytes());
            for (int i = 0; i < 2; i++) {
                fos.write('\t');
            }
            // 写类名信息
            fos.write(clazz.getBytes());
            for (int i = 0; i < 3; i++) {
                fos.write('\t');
            }
            // 写实体属性
            if (superInfo != null){
                for (Map.Entry<String, List<String>> entry : superInfo.entrySet()) {
                    String msg = entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                    fos.write(msg.getBytes());
                    for (int i = 0; i < 3; i++) {
                        fos.write('\t');
                    }
                }
            }
            if (selfInfo != null){
                for (Map.Entry<String, List<String>> entry : selfInfo.entrySet()) {
                    String msg = entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                    fos.write(msg.getBytes());
                    for (int i = 0; i < 3; i++) {
                        fos.write('\t');
                    }
                }
            }
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
                                 String path){
        // 将信息写入文件
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            // 写编码相关提示
            String first = "code: # 编码相关提示 \n";
            fos.write(first.getBytes());
            for (int i = 0; i < 1; i++) {
                fos.write('\t');
            }
            // 写model
            String model = "model：\n";
            fos.write(model.getBytes());
            for (int i = 0; i < 2; i++) {
                fos.write('\t');
            }
            // 写类名信息
            String clazz = classInfo.get(0) + ": " + classInfo.get(1) + "\n";
            fos.write(clazz.getBytes());
            for (int i = 0; i < 3; i++) {
                fos.write('\t');
            }
            // 写实体属性
            String attr = "attribute: # 实体属性 \n";
            fos.write(attr.getBytes());
            for (int i = 0; i < 4; i++) {
                fos.write('\t');
            }
            if (superInfo != null){
                for (Map.Entry<String, List<String>> entry : superInfo.entrySet()) {
                    String msg = entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                    fos.write(msg.getBytes());
                    // 写blank提示
                    if ("true".equals(entry.getValue().get(1))){
                        for (int i = 0; i < 5; i++) {
                            fos.write('\t');
                        }
                        String blank = "blank：请填写" + entry.getValue().get(0) + "! \n";
                        fos.write(blank.getBytes());
                    }
                    for (int i = 0; i < 4; i++) {
                        fos.write('\t');
                    }
                }
            }
            if (selfInfo != null){
                for (Map.Entry<String, List<String>> entry : selfInfo.entrySet()) {
                    String msg = entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                    fos.write(msg.getBytes());
                    // 写blank提示
                    if ("true".equals(entry.getValue().get(1))){
                        for (int i = 0; i < 5; i++) {
                            fos.write('\t');
                        }
                        String blank = "blank：请填写" + entry.getValue().get(0) + "! \n";
                        fos.write(blank.getBytes());
                    }
                    for (int i = 0; i < 4; i++) {
                        fos.write('\t');
                    }
                }
            }
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
    public static Map<String, List<String>> getClassField(Class clazz){

        // 是Object类，跳过
        if (clazz == Object.class) return null;

        Map<String,List<String>> info = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) continue;

            ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
            if (annotation == null) throw new RuntimeException(field.getName() + "属性缺少注解修饰");

            List<String> value = new ArrayList<>();
            value.add(annotation.value());
            value.add(annotation.required() + "");
            info.put(field.getName(),value);
        }

        return info;
    }

}
