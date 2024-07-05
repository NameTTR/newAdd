package com.family.pl.domain.VO;

import com.family.pl.domain.TaskLabel;
import com.family.pl.domain.TaskRemind;
import com.family.pl.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/5/19 18:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskVO {

    /**
     * 任务ID
     */
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
     * 父任务ID，空值则表示一级任务，非空表示是某个任务的子任务
     */
    private Long fatherTaskId;

    /**
     * 标签ID
     */
    private Long labelId;

    /**
     * 标签表
     */
    private List<TaskLabel> taskLabels;

    /**
     * 提醒表
     */
    private List<TaskRemind> taskReminds;

    /**
     * 子任务
     */
    private List<UpdateTaskVO> ChildTasks;

    private static final long serialVersionUID = 1L;
}