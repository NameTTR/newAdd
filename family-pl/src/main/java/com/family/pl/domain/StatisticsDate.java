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
 * <p>
 * 每天统计表
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
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