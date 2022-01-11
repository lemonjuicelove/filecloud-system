package com.github.jfcloud.jos.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/*
    目录展示
 */
@Data
public class DirVo {

    private Long id;
    private Long parentId;
    private String name;

}
