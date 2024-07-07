package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

/**
 * 任务表
 * @TableName pl_task
 */
@TableName(value ="pl_task")
@Data
public class Task implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 备注，包括任务说明和完成任务的备注
     */
    private String notes;

    /**
     * 日期
     */
    private LocalDate taskDate;

    /**
     * 开始时间，允许为空，空值则表示全天
     */
    private LocalTime taskTimeBegin;

    /**
     * 结束时间，允许为空，若开始时间为空，则结束时间不允许非空
     */
    private LocalTime taskTimeEnd;

    /**
     * 重复：0：无；1：每天；2：每月；3：每年；4：工作日；5：法定工作日；6：艾宾浩斯记忆法
     */
    private Integer repeat;

    /**
     * 重复结束时间
     */
    private LocalDateTime repeatEnd;

    /**
     * 优先级：0：无；1；低；2：中；3：高
     */
    private Integer priority;

    /**
     * 是否完成：0：否；1：是（针对已完成任务）
     */
    private Integer isComplete;

    /**
     * 是否全部结束：0：否；1：是
     */
    private Integer isEnd;

    /**
     * 是否有标签：0：否；1：是
     */
    private Integer isLabel;

    /**
     * 是否设置提醒：0：否；1：是
     */
    private Integer isRemind;

    /**
     * 是否有子任务：0：否；1：是
     */
    private Integer isHaveChild;

    /**
     * 是否超时完成：0：否；1：是；结束时间之前不算超时
     */
    private Integer isTimeout;

    /**
     * 父任务ID，空值则表示一级任务，非空表示是某个任务的子任务
     */
    private Long fatherTaskId;

    /**
     * 完成关联任务ID（已完成的任务才需要设置关联任务ID）
     */
    private Long relatedTaskId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    private Integer flagDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}