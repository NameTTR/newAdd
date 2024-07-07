package com.family.pl.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 每周统计表
 * @TableName pl_statistics_week
 */
@TableName(value ="pl_statistics_week")
@Data
public class StatisticsWeek implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 年份
     */
    private Integer taskYear;

    /**
     * 第几周
     */
    private Integer taskWeek;

    /**
     * 开始日期
     */
    private LocalDate beginDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 任务数
     */
    private Integer countTask;

    /**
     * 完成任务数
     */
    private Integer countCompleteTask;

    /**
     * 准时完成的任务数
     */
    private Integer countOntimeTask;

    /**
     * 高优先级任务数
     */
    private Integer countHighTask;

    /**
     * 高优先级完成任务数
     */
    private Integer countCompleteHighTask;

    /**
     * 中优先级任务数
     */
    private Integer countMidTask;

    /**
     * 中优先级完成任务数
     */
    private Integer countCompleteMidTask;

    /**
     * 低优先级任务数
     */
    private Integer countLowTask;

    /**
     * 低优先级完成任务数
     */
    private Integer countCompleteLowTask;

    /**
     * 无优先级任务数
     */
    private Integer countNoTask;

    /**
     * 无优先级完成任务数
     */
    private Integer countCompleteNoTask;

    /**
     * 用户ID
     */
    private Integer userId;

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