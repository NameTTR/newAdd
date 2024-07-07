package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 标签表
 * @TableName pl_label
 */
@TableName(value ="pl_label")
@Data
public class Label implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签
     */
    private String name;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}