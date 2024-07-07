package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 提醒默认表
 * @TableName pl_time_remind_default
 */
@TableName(value ="pl_time_remind_default")
@Data
public class TimeRemindDefault implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 类别：1：按（开始）时间提醒；2：按天提醒

     */
    private Object type;

    /**
     * 按时间提前提醒：0：无；1：准时；2：提前5分钟；3：提前30分钟；4：提前1天
     */
    private Object remindTime;

    /**
     * 按天提前提醒：0：无；1：当天；2：提前1天；3：提前2天；4：提前3天
     */
    private Object remindDate;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}