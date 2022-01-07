package com.github.jfcloud.jos.handler;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
    属性自动填充
 */
@Component
public class TableFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {

        this.setFieldValByName("createdDate",new Date(),metaObject);
        this.setFieldValByName("status","1",metaObject);
        this.setFieldValByName("deletedFlag","1",metaObject);
        this.setFieldValByName("permissions",777,metaObject);
        this.setFieldValByName("id", IdUtil.getSnowflakeNextId(),metaObject);
        this.setFieldValByName("createdBy", 19980218L,metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("lastModifiedDate",new Date(),metaObject);
        this.setFieldValByName("lastModifiedBy",19980218L,metaObject);
    }

}
