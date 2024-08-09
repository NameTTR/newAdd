package com.family.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mybatis-Plus LocalDateTime 插入更新自动填充
 *
 * 注意：
 * 1.不要使用lambdaUpdate().-一系列操作-.update(更新后的实体类)方法，否则LocalDateTime字段不会自动填充。
 * 把需要更改的信息再（一系列操作）种完成，然后再 .update()
 *
 * 2.当使用update..(更新后的实体类)、delete..(更新后的实体类)、save..(更新后的实体类)、insert..(更新后的实体类)方法时
 *同上一样不能直接将 ”更新后的实体类“ 放到这些方法中。  个人建议：都用lambdaUpdate()
 *
 * 3.需要在实体类中加@TableField(fill = FieldFill.INSERT_UPDATE)注解
 * 例子：
 * @TableField(fill = FieldFill.UPDATE)
 * private LocalDateTime createdTime;
 *
 * @TableField(fill = FieldFill.UPDATE)
 * private LocalDateTime updateTime;
 */
@Component
public class LocalDateTimeHandler implements MetaObjectHandler {

    /**
     * 插入时
     * 自动填充 LocalDateTime 字段
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新时
     * 自动填充 LocalDateTime 字段
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
