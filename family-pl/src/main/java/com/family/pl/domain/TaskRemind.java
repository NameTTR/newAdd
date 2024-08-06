package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 任务提醒表
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@TableName(value ="pl_task_remind")
@Data
public class TaskRemind implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务表ID
     */
    private Long taskId;

    /**
     * 类别：1：按（开始）时间提醒；2：按天提醒

     */
    private Integer type;

    /**
     * 按时间提前提醒：0：无；1：准时；2：提前5分钟；3：提前30分钟；4：提前1天
     */
    private Integer remindByTime;

    /**
     * 按天提前提醒：0：无；1：当天；2：提前1天；3：提前2天；4：提前3天
     */
    private Integer remindByDate;

    /**
     * corn字符串
     */
    private String corn;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}