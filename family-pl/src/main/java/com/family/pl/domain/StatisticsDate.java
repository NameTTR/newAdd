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
 * 每天统计表
 * @TableName pl_statistics_date
 */
@TableName(value ="pl_statistics_date")
@Data
public class StatisticsDate implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 日期
     */
    private LocalDate taskDate;

    /**
     * 任务数
     */
    private Integer countTask;

    /**
     * 完成任务数
     */
    private Integer countCompleteTask;

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